package com.huariservice.huariia.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huariservice.huariia.dto.ParliamentarianExpenseResponse;
import com.huariservice.huariia.dto.cnpj.CnpjCompanyResponse;
import com.huariservice.huariia.dto.cnpj.ExpenseSupplierAuditResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CnpjIntegrationServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ObjectMapper objectMapper;
    private CnpjIntegrationService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new CnpjIntegrationService(
                restClient,
                objectMapper,
                "https://brasilapi.com.br/api"
        );
    }

    @Test
    @DisplayName("Should validate CNPJ correctly and reject invalid lengths or identical digits")
    void shouldValidateCnpj() {
        assertEquals("00000000000191", CnpjIntegrationService.sanitizeAndValidateCnpj("00.000.000/0001-91"));
        assertEquals("02012862000160", CnpjIntegrationService.sanitizeAndValidateCnpj("02012862000160"));

        assertThrows(ResponseStatusException.class, () -> CnpjIntegrationService.sanitizeAndValidateCnpj("123"));
        assertThrows(ResponseStatusException.class, () -> CnpjIntegrationService.sanitizeAndValidateCnpj("11111111111111"));
        assertThrows(ResponseStatusException.class, () -> CnpjIntegrationService.sanitizeAndValidateCnpj(""));
        assertThrows(ResponseStatusException.class, () -> CnpjIntegrationService.sanitizeAndValidateCnpj(null));
    }

    @Test
    @DisplayName("Should fetch and parse company registration data with partners and clean alerts from BrasilAPI")
    void shouldFetchCompanySuccessfully() {
        String json = """
                {
                  "cnpj": "00000000000191",
                  "razao_social": "BANCO DO BRASIL SA",
                  "nome_fantasia": "DIRECAO GERAL",
                  "descricao_situacao_cadastral": "ATIVA",
                  "data_situacao_cadastral": "2005-11-03",
                  "data_inicio_atividade": "1966-08-01",
                  "cnae_fiscal": 6422100,
                  "cnae_fiscal_descricao": "Bancos multiplos",
                  "capital_social": 12000000000.00,
                  "municipio": "BRASILIA",
                  "uf": "DF",
                  "qsa": [
                    {
                      "nome_socio": "TARCISIO JOSE",
                      "qualificacao_socio": "Diretor",
                      "faixa_etaria": "Entre 61 a 70 anos",
                      "data_entrada_sociedade": "2019-04-18"
                    }
                  ]
                }
                """;

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(json);

        CnpjCompanyResponse response = service.fetchCompanyByCnpj("00.000.000/0001-91");

        assertNotNull(response);
        assertEquals("ONLINE", response.status());
        assertEquals("00000000000191", response.cnpj());
        assertEquals("00.000.000/0001-91", response.cnpjFormatado());
        assertEquals("BANCO DO BRASIL SA", response.razaoSocial());
        assertEquals("ATIVA", response.situacaoCadastral());
        assertEquals(1, response.qsa().size());
        assertEquals("TARCISIO JOSE", response.qsa().getFirst().nomeSocio());
        assertTrue(response.alertasTransparencia().isEmpty(), "Active large company should have no anomaly alerts");
    }

    @Test
    @DisplayName("Should generate transparency alerts for inactive company and low capital social")
    void shouldGenerateAlertsForAnomalies() {
        String json = """
                {
                  "cnpj": "99999999000199",
                  "razao_social": "FACHADA SERVICOS LTDA",
                  "nome_fantasia": "FACHADA",
                  "descricao_situacao_cadastral": "BAIXADA",
                  "data_inicio_atividade": "2026-10-01",
                  "capital_social": 500.00,
                  "municipio": "SAO PAULO",
                  "uf": "SP",
                  "qsa": []
                }
                """;

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(json);

        CnpjCompanyResponse response = service.fetchCompanyByCnpj("99999999000199");

        assertNotNull(response);
        assertFalse(response.alertasTransparencia().isEmpty());
        assertTrue(response.alertasTransparencia().stream().anyMatch(a -> a.contains("BAIXADA")));
        assertTrue(response.alertasTransparencia().stream().anyMatch(a -> a.contains("recém-criada")));
        assertTrue(response.alertasTransparencia().stream().anyMatch(a -> a.contains("muito reduzido")));
    }

    @Test
    @DisplayName("Should return contingency fallback company when BrasilAPI times out")
    void shouldFallbackOnNetworkTimeout() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Connection timed out"));

        CnpjCompanyResponse response = service.fetchCompanyByCnpj("02012862000160");

        assertNotNull(response);
        assertEquals("FALLBACK", response.status());
        assertEquals("02012862000160", response.cnpj());
        assertNotNull(response.razaoSocial());
        assertFalse(response.qsa().isEmpty());
    }

    @Test
    @DisplayName("Should cross-audit expense and detect critical risk when expense amount exceeds company capital social")
    void shouldAuditExpenseSupplierWithHighRisk() {
        String json = """
                {
                  "cnpj": "12345678000195",
                  "razao_social": "MICRO EMPRESA LTDA",
                  "descricao_situacao_cadastral": "ATIVA",
                  "data_inicio_atividade": "2026-01-01",
                  "capital_social": 1000.00,
                  "municipio": "BRASILIA",
                  "uf": "DF",
                  "qsa": []
                }
                """;

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(json);

        ParliamentarianExpenseResponse expense = new ParliamentarianExpenseResponse(
                1L, 10L, "Deputado Exemplo", "Consultorias",
                "MICRO EMPRESA LTDA (12.345.678/0001-95)",
                new BigDecimal("50000.00"), LocalDate.now(), "http://doc.pdf"
        );

        ExpenseSupplierAuditResponse audit = service.auditExpenseSupplier(expense, null);

        assertNotNull(audit);
        assertEquals("ALTO", audit.nivelRisco());
        assertTrue(audit.alertasConsolidados().stream().anyMatch(a -> a.contains("excede o capital social")));
    }
}

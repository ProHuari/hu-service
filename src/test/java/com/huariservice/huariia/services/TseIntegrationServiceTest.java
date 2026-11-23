package com.huariservice.huariia.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huariservice.huariia.dto.tse.TseCandidateAssetsResponse;
import com.huariservice.huariia.dto.tse.TseCandidateSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TseIntegrationServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ObjectMapper objectMapper;
    private TseIntegrationService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new TseIntegrationService(
                restClient,
                objectMapper,
                "https://divulgacandcontas.tse.jus.br/divulga/rest/v1"
        );
    }

    @Test
    @DisplayName("Should fetch and parse candidate list successfully from TSE API")
    void shouldFetchCandidatesSuccessfully() {
        String json = """
                {
                  "candidatos": [
                    {
                      "id": 250001600123,
                      "nomeUrna": "Tabata Amaral",
                      "nomeCompleto": "Tabata Claudia Amaral de Pontes",
                      "numero": 4000,
                      "partido": "PSB",
                      "cargo": { "nome": "Deputado Federal" },
                      "siglaUf": "SP",
                      "totalDeBens": 807446.50,
                      "descricaoSituacao": "DEFERIDO"
                    }
                  ]
                }
                """;

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(json);

        List<TseCandidateSummaryDto> list = service.fetchCandidates("Tabata", "SP", 2022, "deputado");

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Tabata Amaral", list.getFirst().nomeUrna());
        assertEquals("PSB", list.getFirst().siglaPartido());
        assertEquals(0, new BigDecimal("807446.50").compareTo(list.getFirst().totalBens()));
    }

    @Test
    @DisplayName("Should return contingency fallback candidates when TSE API throws timeout")
    void shouldFallbackCandidatesOnNetworkTimeout() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("TSE API timeout"));

        List<TseCandidateSummaryDto> list = service.fetchCandidates(null, "SP", 2022, null);

        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(c -> "Tabata Amaral".equals(c.nomeUrna())));
    }

    @Test
    @DisplayName("Should fetch candidate assets, calculate total, and map asset items from TSE API")
    void shouldFetchCandidateAssetsSuccessfully() {
        String json = """
                {
                  "id": 250001600123,
                  "nomeUrna": "Tabata Amaral",
                  "nomeCompleto": "Tabata Claudia Amaral de Pontes",
                  "siglaPartido": "PSB",
                  "cargo": { "nome": "Deputado Federal" },
                  "uf": "SP",
                  "totalDeBens": 807446.50,
                  "bens": [
                    {
                      "descricao": "Apartamento em Sao Paulo",
                      "descricaoDeTipoDeBem": "Apartamento",
                      "valor": 600000.00
                    },
                    {
                      "descricao": "Fundo de renda fixa",
                      "descricaoDeTipoDeBem": "Aplicacao financeira",
                      "valor": 207446.50
                    }
                  ]
                }
                """;

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(json);

        TseCandidateAssetsResponse response = service.fetchCandidateAssets(250001600123L, 2022, "SP");

        assertNotNull(response);
        assertEquals("ONLINE", response.status());
        assertEquals(250001600123L, response.candidatoId());
        assertEquals(0, new BigDecimal("807446.50").compareTo(response.valorTotalBens()));
        assertEquals(2, response.quantidadeBens());
        assertEquals(2, response.bens().size());
        assertEquals("Apartamento em Sao Paulo", response.bens().getFirst().descricao());
    }

    @Test
    @DisplayName("Should return contingency fallback assets when TSE API returns error or timeout")
    void shouldFallbackAssetsOnNetworkFailure() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Connection timed out"));

        TseCandidateAssetsResponse response = service.fetchCandidateAssets(250001600123L, 2022, "SP");

        assertNotNull(response);
        assertEquals("FALLBACK", response.status());
        assertEquals(250001600123L, response.candidatoId());
        assertNotNull(response.valorTotalBens());
        assertFalse(response.bens().isEmpty());
    }
}

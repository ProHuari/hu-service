package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.camara.CamaraDeputyDto;
import com.huariservice.huariia.dto.camara.CamaraExpenseDto;
import com.huariservice.huariia.dto.camara.CamaraPropositionDto;
import com.huariservice.huariia.dto.camara.CamaraSyncSummaryResponse;
import com.huariservice.huariia.dto.camara.CamaraWrapperResponse;
import com.huariservice.huariia.entities.Parliamentarian;
import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import com.huariservice.huariia.repositories.ParliamentarianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CamaraIntegrationServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ParliamentarianRepository parliamentarianRepository;

    private CamaraIntegrationService service;

    @BeforeEach
    void setUp() {
        service = new CamaraIntegrationService(
                restClient,
                parliamentarianRepository,
                "https://dadosabertos.camara.leg.br/api/v2"
        );
    }

    @Test
    @DisplayName("Should fetch deputies successfully from Camara API")
    void shouldFetchDeputiesSuccessfully() {
        CamaraDeputyDto deputy = new CamaraDeputyDto(
                220593L,
                "https://dadosabertos.camara.leg.br/api/v2/deputados/220593",
                "Erika Hilton",
                "PSOL",
                "https://dadosabertos.camara.leg.br/api/v2/partidos/36839",
                "SP",
                57,
                "https://www.camara.leg.br/internet/deputado/bandep/220593.jpg",
                "dep.erikahilton@camara.leg.br"
        );
        CamaraWrapperResponse<CamaraDeputyDto> wrapper = new CamaraWrapperResponse<>(List.of(deputy), List.of());

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(wrapper);

        List<CamaraDeputyDto> result = service.fetchDeputies("Erika", "SP", "PSOL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Erika Hilton", result.get(0).nome());
        assertEquals("SP", result.get(0).siglaUf());
    }

    @Test
    @DisplayName("Should return empty list on network failure when fetching deputies")
    void shouldReturnEmptyListWhenFetchingDeputiesFails() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Connection timed out"));

        List<CamaraDeputyDto> result = service.fetchDeputies("Silva", null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should fetch expenses for deputy successfully")
    void shouldFetchExpensesSuccessfully() {
        CamaraExpenseDto expense = new CamaraExpenseDto(
                2026,
                3,
                "COMBUSTÍVEIS E LUBRIFICANTES",
                12345L,
                "Nota Fiscal Eletrônica",
                0,
                "2026-03-10",
                "9876",
                new BigDecimal("250.00"),
                "https://documento.camara.leg.br/nf/12345",
                "Posto Brasil",
                "00.000.000/0001-91",
                new BigDecimal("250.00"),
                BigDecimal.ZERO,
                "123",
                1,
                0
        );
        CamaraWrapperResponse<CamaraExpenseDto> wrapper = new CamaraWrapperResponse<>(List.of(expense), List.of());

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(wrapper);

        List<CamaraExpenseDto> result = service.fetchDeputyExpenses(220593L, 2026, 3);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("250.00"), result.get(0).valorLiquido());
    }

    @Test
    @DisplayName("Should return empty list when deputy ID is null")
    void shouldReturnEmptyListWhenDeputyIdNull() {
        List<CamaraExpenseDto> result = service.fetchDeputyExpenses(null, 2026, null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should fetch propositions successfully")
    void shouldFetchPropositionsSuccessfully() {
        CamaraPropositionDto prop = new CamaraPropositionDto(
                234567L,
                "https://dadosabertos.camara.leg.br/api/v2/proposicoes/234567",
                "PL",
                139,
                1234,
                2026,
                "Dispõe sobre a transparência no uso de inteligência artificial pelo poder público."
        );
        CamaraWrapperResponse<CamaraPropositionDto> wrapper = new CamaraWrapperResponse<>(List.of(prop), List.of());

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(wrapper);

        List<CamaraPropositionDto> result = service.fetchPropositions("PL", 2026, "inteligência");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PL", result.get(0).siglaTipo());
    }

    @Test
    @DisplayName("Should synchronize deputies to database without duplicates")
    void shouldSyncDeputiesToDatabaseWithoutDuplicates() {
        CamaraDeputyDto remote1 = new CamaraDeputyDto(
                1001L, "", "Deputado Novo", "PARTIDO", "", "SP", 57, "", ""
        );
        CamaraDeputyDto remote2 = new CamaraDeputyDto(
                1002L, "", "Deputado Existente Atualizado", "NOVO_PARTIDO", "", "RJ", 57, "", ""
        );
        CamaraWrapperResponse<CamaraDeputyDto> wrapper = new CamaraWrapperResponse<>(List.of(remote1, remote2), List.of());

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(wrapper);

        Parliamentarian existing = new Parliamentarian();
        existing.setId(99L);
        existing.setExternalId("1002");
        existing.setName("Deputado Antigo");
        existing.setParty("ANTIGO_PARTIDO");
        existing.setState("RJ");
        existing.setChamber(ParliamentarianChamber.CHAMBER_OF_DEPUTIES);

        when(parliamentarianRepository.findByExternalIdAndChamber("1001", ParliamentarianChamber.CHAMBER_OF_DEPUTIES))
                .thenReturn(Optional.empty());
        when(parliamentarianRepository.findByExternalIdAndChamber("1002", ParliamentarianChamber.CHAMBER_OF_DEPUTIES))
                .thenReturn(Optional.of(existing));

        CamaraSyncSummaryResponse summary = service.syncDeputiesToDatabase(null, 10);

        assertNotNull(summary);
        assertEquals("SUCCESS", summary.status());
        assertEquals(2, summary.totalFound());
        assertEquals(1, summary.createdCount());
        assertEquals(1, summary.updatedCount());
        verify(parliamentarianRepository, times(2)).save(any(Parliamentarian.class));
    }

    @Test
    @DisplayName("Should return fallback summary when sync fails due to empty response or remote error")
    void shouldReturnFallbackWhenSyncFails() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Connection timed out"));

        CamaraSyncSummaryResponse summary = service.syncDeputiesToDatabase("SP", 10);

        assertNotNull(summary);
        assertEquals("DEGRADED", summary.status());
        assertEquals(0, summary.totalFound());
        verify(parliamentarianRepository, never()).save(any(Parliamentarian.class));
    }
}

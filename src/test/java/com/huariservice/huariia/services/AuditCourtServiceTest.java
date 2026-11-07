package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.auditcourt.AuditCourtSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditCourtServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Test
    @DisplayName("Should return resilient fallback response when Audit Court API fails with timeout")
    void shouldReturnFallbackOnNetworkFailure() {
        AuditCourtService service = new AuditCourtService(restClient, "https://dadosabertos.tcu.gov.br/api");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Connection timed out"));

        AuditCourtSummaryResponse response = service.fetchTransparencyOverview("educacao");

        assertNotNull(response);
        assertEquals("DEGRADED", response.status());
        assertEquals(0, response.totalItems());
        assertTrue(response.processes().isEmpty());
        assertTrue(response.irregularAccounts().isEmpty());
        assertTrue(response.message().contains("temporariamente indisponível"));
    }

    @Test
    @DisplayName("Should return empty list gracefully when processes query fails")
    void shouldReturnEmptyListWhenProcessesQueryFails() {
        AuditCourtService service = new AuditCourtService(restClient, "https://dadosabertos.tcu.gov.br/api");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Timeout"));

        var processes = service.fetchProcesses("saude", 10);
        assertNotNull(processes);
        assertTrue(processes.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list gracefully when irregular accounts query fails")
    void shouldReturnEmptyListWhenIrregularAccountsQueryFails() {
        AuditCourtService service = new AuditCourtService(restClient, "https://dadosabertos.tcu.gov.br/api");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Service error"));

        var irregular = service.fetchIrregularAccounts("12345678900");
        assertNotNull(irregular);
        assertTrue(irregular.isEmpty());
    }
}


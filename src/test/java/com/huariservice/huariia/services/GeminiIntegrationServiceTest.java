package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.ai.AiTutorChatRequest;
import com.huariservice.huariia.dto.ai.AiTutorChatResponse;
import com.huariservice.huariia.dto.ai.ExplainBillRequest;
import com.huariservice.huariia.dto.ai.ExplainBillResponse;
import com.huariservice.huariia.entities.AiHistory;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.repositories.AiHistoryRepository;
import com.huariservice.huariia.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeminiIntegrationServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private AiHistoryRepository aiHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return structured fallback simulation for explainBill when API key is missing")
    void shouldReturnFallbackSimulationWhenApiKeyMissing() {
        GeminiIntegrationService service = new GeminiIntegrationService(
                restClient,
                aiHistoryRepository,
                userRepository,
                "", // empty API key
                "gemini-2.5-flash"
        );

        assertFalse(service.isApiKeyConfigured());

        ExplainBillRequest request = new ExplainBillRequest(
                "PL 1234/2026",
                "Dispõe sobre a transparência no uso de IA nos serviços públicos.",
                "Deputado Exemplo",
                "Tecnologia"
        );

        ExplainBillResponse response = service.explainBill(request, null);

        assertNotNull(response);
        assertEquals("PL 1234/2026", response.billCode());
        assertEquals("FALLBACK_SIMULATION", response.mode());
        assertNotNull(response.simplifiedExplanation());
        assertNotNull(response.practicalImpacts());
        assertFalse(response.pointsInFavor().isEmpty());
        assertFalse(response.pointsAgainst().isEmpty());
        verifyNoInteractions(restClient);
    }

    @Test
    @DisplayName("Should persist fallback interaction into AiHistory when user is authenticated")
    void shouldPersistHistoryWhenUserAuthenticated() {
        GeminiIntegrationService service = new GeminiIntegrationService(
                restClient,
                aiHistoryRepository,
                userRepository,
                "", // empty API key
                "gemini-2.5-flash"
        );

        User mockUser = new User();
        mockUser.setId(42L);

        when(userRepository.findById(42L)).thenReturn(Optional.of(mockUser));
        when(aiHistoryRepository.save(any(AiHistory.class))).thenAnswer(invocation -> {
            AiHistory history = invocation.getArgument(0);
            history.setId(999L);
            return history;
        });

        AiTutorChatRequest request = new AiTutorChatRequest(
                "O que é a Cota Parlamentar?",
                "Orçamento Público",
                "Aula de Introdução"
        );

        AiTutorChatResponse response = service.askTutor(request, 42L);

        assertNotNull(response);
        assertEquals("FALLBACK_SIMULATION", response.mode());
        assertEquals(999L, response.historyId());
        verify(aiHistoryRepository, times(1)).save(any(AiHistory.class));
    }

    @Test
    @DisplayName("Should fallback gracefully to simulation when Gemini API call fails with timeout")
    void shouldFallbackWhenGeminiApiFails() {
        GeminiIntegrationService service = new GeminiIntegrationService(
                restClient,
                aiHistoryRepository,
                userRepository,
                "real-api-key-test",
                "gemini-2.5-flash"
        );

        assertTrue(service.isApiKeyConfigured());

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenThrow(new ResourceAccessException("Gemini connection timed out"));

        ExplainBillRequest request = new ExplainBillRequest(
                "PL 9999/2026",
                "Regulamenta incentivos à educação digital cidadã.",
                null,
                null
        );

        ExplainBillResponse response = service.explainBill(request, null);

        assertNotNull(response);
        assertEquals("FALLBACK_SIMULATION", response.mode());
        assertTrue(response.simplifiedExplanation().contains("PL 9999/2026"));
    }
}

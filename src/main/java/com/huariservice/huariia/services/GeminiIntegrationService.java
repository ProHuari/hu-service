package com.huariservice.huariia.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huariservice.huariia.dto.ai.AiTutorChatRequest;
import com.huariservice.huariia.dto.ai.AiTutorChatResponse;
import com.huariservice.huariia.dto.ai.ExplainBillRequest;
import com.huariservice.huariia.dto.ai.ExplainBillResponse;
import com.huariservice.huariia.entities.AiHistory;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.repositories.AiHistoryRepository;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service managing real generative AI operations with Google Gemini (gemini-2.5-flash)
 * alongside intelligent local fallbacks for development environments lacking an API key.
 */
@Slf4j
@Service
public class GeminiIntegrationService {

    private static final String TUTOR_SYSTEM_INSTRUCTION = """
            Você é o Tutor Cidadão da plataforma Huari.ia. \
            Sua missão é explicar conceitos de cidadania, políticas públicas, funcionamento dos \
            poderes e matérias legislativas com linguagem simples, apartidária, ética e didática, \
            adequada a estudantes e cidadãos brasileiros. \
            Sempre encoraje a participação cívica consciente e o controle social dos gastos públicos.""";

    private final RestClient restClient;
    private final AiHistoryRepository aiHistoryRepository;
    private final UserRepository userRepository;
    private final String apiKey;
    private final String model;

    public GeminiIntegrationService(
            @Qualifier("geminiRestClient") RestClient restClient,
            AiHistoryRepository aiHistoryRepository,
            UserRepository userRepository,
            @Value("${app.ai.gemini.api-key:}") String apiKey,
            @Value("${app.ai.gemini.model:gemini-2.5-flash}") String model) {
        this.restClient = restClient;
        this.aiHistoryRepository = aiHistoryRepository;
        this.userRepository = userRepository;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null && !model.isBlank() ? model.trim() : "gemini-2.5-flash";
    }

    /**
     * Checks whether a non-empty Google Gemini API key is configured.
     */
    public boolean isApiKeyConfigured() {
        return !apiKey.isBlank() && !apiKey.equalsIgnoreCase("sua_chave_aqui");
    }

    /**
     * Simplifies a legislative bill's formal text ("juridiques") for citizens.
     */
    @Transactional
    public ExplainBillResponse explainBill(ExplainBillRequest request, Long authenticatedUserId) {
        if (!isApiKeyConfigured()) {
            log.info("GEMINI_API_KEY not configured. Using structured fallback simulation for bill: {}", request.billCode());
            return buildFallbackBillExplanation(request, authenticatedUserId);
        }

        try {
            String prompt = String.format("""
                    Analise e traduza a seguinte ementa de Projeto de Lei do 'juridiquês' para português simples:
                    Código do Projeto: %s
                    Ementa Original: %s
                    Autor/Tema: %s / %s
                    
                    Forneça uma explicação direta em linguagem cidadã, destacando o objetivo real e como isso afeta a vida prática das pessoas.
                    """,
                    request.billCode(),
                    request.ementa(),
                    request.author() != null ? request.author() : "Não informado",
                    request.theme() != null ? request.theme() : "Geral"
            );

            String generatedText = callGeminiGenerateContent(prompt, TUTOR_SYSTEM_INSTRUCTION);

            if (generatedText == null || generatedText.isBlank()) {
                return buildFallbackBillExplanation(request, authenticatedUserId);
            }

            String practicalImpacts = "Impacto direto na transparência e aplicação das diretrizes estabelecidas pelo texto da lei para a sociedade.";
            List<String> pros = List.of(
                    "Aumenta a clareza e previsibilidade das normas para a população",
                    "Fortalece o debate democrático e a fiscalização cidadã",
                    "Moderniza procedimentos legislativos e administrativos"
            );
            List<String> cons = List.of(
                    "Demanda adaptação de órgãos públicos e regulamentação complementar",
                    "Necessidade de monitoramento contínuo dos custos de implementação"
            );

            recordHistoryIfUserAuthenticated(
                    authenticatedUserId,
                    "Explicar Projeto de Lei " + request.billCode() + ": " + request.ementa(),
                    generatedText
            );

            return ExplainBillResponse.ofReal(
                    request.billCode(),
                    request.ementa(),
                    generatedText,
                    practicalImpacts,
                    pros,
                    cons
            );

        } catch (Exception ex) {
            log.warn("Gemini API call failed for bill explanation. Activating fallback simulation: {}", ex.getMessage());
            return buildFallbackBillExplanation(request, authenticatedUserId);
        }
    }

    /**
     * Answers citizen questions about democracy, citizenship, public transparency, or course topics.
     */
    @Transactional
    public AiTutorChatResponse askTutor(AiTutorChatRequest request, Long authenticatedUserId) {
        if (!isApiKeyConfigured()) {
            log.info("GEMINI_API_KEY not configured. Using structured fallback simulation for tutor query: {}", request.question());
            return buildFallbackTutorAnswer(request, authenticatedUserId);
        }

        try {
            String prompt = request.question();
            if (request.lessonContext() != null && !request.lessonContext().isBlank()) {
                prompt = "[Contexto da Aula: " + request.lessonContext() + "] " + prompt;
            }

            String generatedText = callGeminiGenerateContent(prompt, TUTOR_SYSTEM_INSTRUCTION);

            if (generatedText == null || generatedText.isBlank()) {
                return buildFallbackTutorAnswer(request, authenticatedUserId);
            }

            Long historyId = recordHistoryIfUserAuthenticated(
                    authenticatedUserId,
                    request.question(),
                    generatedText
            );

            List<String> topics = List.of(
                    "Acompanhamento de votações na Câmara e Senado",
                    "Fiscalização e transparência de recursos públicos",
                    "Canais de ouvidoria e participação popular"
            );

            return AiTutorChatResponse.ofReal(
                    request.question(),
                    generatedText,
                    request.subject() != null ? request.subject() : "Cidadania e Política",
                    topics,
                    historyId
            );

        } catch (Exception ex) {
            log.warn("Gemini API call failed for tutor. Activating fallback simulation: {}", ex.getMessage());
            return buildFallbackTutorAnswer(request, authenticatedUserId);
        }
    }

    private String callGeminiGenerateContent(String userPrompt, String systemInstruction) {
        GeminiPayload payload = new GeminiPayload(
                new SystemContent(List.of(new ContentPart(systemInstruction))),
                List.of(new Content(List.of(new ContentPart(userPrompt))))
        );

        GeminiResult result = restClient.post()
                .uri("/models/{model}:generateContent", model)
                .body(payload)
                .retrieve()
                .body(GeminiResult.class);

        if (result == null || result.candidates() == null || result.candidates().isEmpty()) {
            return null;
        }

        Candidate candidate = result.candidates().get(0);
        if (candidate.content() == null || candidate.content().parts() == null) {
            return null;
        }

        return candidate.content().parts().stream()
                .map(ContentPart::text)
                .filter(t -> t != null && !t.isBlank())
                .reduce("", String::concat)
                .trim();
    }

    private ExplainBillResponse buildFallbackBillExplanation(ExplainBillRequest request, Long authenticatedUserId) {
        String simplified = String.format(
                "Em linguagem simples para o cidadão: o projeto de lei %s visa regulamentar diretamente o seguinte tema: \"%s\". "
                        + "O objetivo principal desta proposta é estabelecer normas mais claras para a sociedade, "
                        + "garantindo que direitos e deveres fiquem evidentes tanto para os órgãos públicos quanto para os cidadãos.",
                request.billCode(),
                request.ementa()
        );

        String practicalImpacts = "No dia a dia, esta proposta impacta o acesso a serviços públicos, estabelece prazos mais definidos e facilita o controle social e fiscalização das ações governamentais.";

        List<String> pros = List.of(
                "Maior transparência e segurança jurídica para o cidadão",
                "Padronização de regras aplicadas no âmbito nacional",
                "Facilita o acompanhamento e cobrança de resultados pelos cidadãos"
        );

        List<String> cons = List.of(
                "Pode exigir adequação orçamentária dos entes públicos para implementação plena",
                "Exige fiscalização atenta para evitar burocratização excessiva"
        );

        recordHistoryIfUserAuthenticated(
                authenticatedUserId,
                "Explicar Projeto de Lei " + request.billCode() + ": " + request.ementa(),
                simplified
        );

        return ExplainBillResponse.ofSimulation(
                request.billCode(),
                request.ementa(),
                simplified,
                practicalImpacts,
                pros,
                cons
        );
    }

    private AiTutorChatResponse buildFallbackTutorAnswer(AiTutorChatRequest request, Long authenticatedUserId) {
        String simulatedAnswer = String.format(
                "Olá! Sou o Tutor Cidadão Huari. Sobre sua dúvida (\"%s\"): "
                        + "No contexto da educação política e da cidadania brasileira, compreender o funcionamento dos órgãos públicos "
                        + "e a destinação dos recursos é fundamental. O poder público atua como representante do cidadão, e mecanismos de transparência "
                        + "permitem fiscalizar projetos de lei, orçamentos e a atuação de deputados e senadores para fortalecer a democracia.",
                request.question()
        );

        Long historyId = recordHistoryIfUserAuthenticated(
                authenticatedUserId,
                request.question(),
                simulatedAnswer
        );

        List<String> topics = List.of(
                "Como funciona o processo de aprovação de leis no Brasil",
                "O papel do Tribunal de Contas na fiscalização de gastos",
                "Cidadania ativa: como sugerir ou apoiar projetos de lei"
        );

        return AiTutorChatResponse.ofSimulation(
                request.question(),
                simulatedAnswer,
                request.subject() != null ? request.subject() : "Cidadania e Democracia",
                topics,
                historyId
        );
    }

    private Long recordHistoryIfUserAuthenticated(Long userId, String question, String answer) {
        if (userId == null) {
            return null;
        }
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return null;
            }
            AiHistory history = new AiHistory();
            history.setUser(user);
            history.setQuestion(question);
            history.setAnswer(answer);
            history.setQueriedAt(LocalDateTime.now());
            AiHistory saved = aiHistoryRepository.save(history);
            return saved.getId();
        } catch (Exception ex) {
            log.warn("Could not persist interaction to AiHistory: {}", ex.getMessage());
            return null;
        }
    }

    // JSON mapping internal records for Gemini API
    private record ContentPart(String text) {}
    private record Content(List<ContentPart> parts) {}
    private record SystemContent(@JsonProperty("parts") List<ContentPart> parts) {}
    private record GeminiPayload(
            @JsonProperty("systemInstruction") SystemContent systemInstruction,
            List<Content> contents) {}
    private record Candidate(Content content) {}
    private record GeminiResult(List<Candidate> candidates) {}
}

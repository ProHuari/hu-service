package com.huariservice.huariia.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huariservice.huariia.dto.ai.AiTutorChatRequest;
import com.huariservice.huariia.dto.ai.AiTutorChatResponse;
import com.huariservice.huariia.dto.ai.ExplainBillRequest;
import com.huariservice.huariia.dto.ai.ExplainBillResponse;
import com.huariservice.huariia.dto.ai.GeminiApiDtos.GeminiRequest;
import com.huariservice.huariia.dto.ai.GeminiApiDtos.GeminiResponse;
import com.huariservice.huariia.entities.AiHistory;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.repositories.AiHistoryRepository;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing real generative AI operations with Google Gemini (gemini-2.5-flash)
 * using Structured JSON Output ({@code responseMimeType: "application/json"}), alongside
 * intelligent local fallbacks for development environments lacking an API key.
 *
 * <p>External HTTP calls execute outside database transactions; history persistence uses
 * repository-level transactions only after the AI response is obtained.</p>
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
    private final PoliticalTransparencyService politicalTransparencyService;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiIntegrationService(
            @Qualifier("geminiRestClient") RestClient restClient,
            AiHistoryRepository aiHistoryRepository,
            UserRepository userRepository,
            @Value("${app.ai.gemini.api-key:}") String apiKey,
            @Value("${app.ai.gemini.model:gemini-2.5-flash}") String model) {
        this(restClient, aiHistoryRepository, userRepository, null, new ObjectMapper(), apiKey, model);
    }

    @Autowired
    public GeminiIntegrationService(
            @Qualifier("geminiRestClient") RestClient restClient,
            AiHistoryRepository aiHistoryRepository,
            UserRepository userRepository,
            @Lazy PoliticalTransparencyService politicalTransparencyService,
            @Value("${app.ai.gemini.api-key:}") String apiKey,
            @Value("${app.ai.gemini.model:gemini-2.5-flash}") String model) {
        this(restClient, aiHistoryRepository, userRepository, politicalTransparencyService, new ObjectMapper(), apiKey, model);
    }

    GeminiIntegrationService(
            RestClient restClient,
            AiHistoryRepository aiHistoryRepository,
            UserRepository userRepository,
            PoliticalTransparencyService politicalTransparencyService,
            ObjectMapper objectMapper,
            String apiKey,
            String model) {
        this.restClient = restClient;
        this.aiHistoryRepository = aiHistoryRepository;
        this.userRepository = userRepository;
        this.politicalTransparencyService = politicalTransparencyService;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
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
     * Simplifies a legislative bill's formal text ("juridiquês") into structured citizen-friendly JSON.
     */
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

                    Responda EXCLUSIVAMENTE com um objeto JSON válido contendo exatamente as chaves:
                    {
                      "simplifiedExplanation": "Explicação clara e direta em linguagem cidadã",
                      "practicalImpacts": "Como este projeto afeta o dia a dia das pessoas e dos serviços públicos",
                      "pointsInFavor": ["Argumento favorável 1 específico do PL", "Argumento favorável 2", "Argumento favorável 3"],
                      "pointsAgainst": ["Crítica ou ponto de atenção 1 específico do PL", "Crítica ou ponto de atenção 2"]
                    }
                    """,
                    request.billCode(),
                    request.ementa(),
                    request.author() != null ? request.author() : "Não informado",
                    request.theme() != null ? request.theme() : "Geral"
            );

            String rawJson = callGeminiStructuredJson(prompt, TUTOR_SYSTEM_INSTRUCTION);
            if (rawJson == null || rawJson.isBlank()) {
                return buildFallbackBillExplanation(request, authenticatedUserId);
            }

            StructuredBillAnalysis parsed = parseBillAnalysis(rawJson);
            if (parsed == null || parsed.simplifiedExplanation() == null || parsed.simplifiedExplanation().isBlank()) {
                return buildFallbackBillExplanation(request, authenticatedUserId);
            }

            recordHistoryIfUserAuthenticated(
                    authenticatedUserId,
                    "Explicar Projeto de Lei " + request.billCode() + ": " + request.ementa(),
                    parsed.simplifiedExplanation()
            );

            return ExplainBillResponse.ofReal(
                    request.billCode(),
                    request.ementa(),
                    parsed.simplifiedExplanation(),
                    parsed.practicalImpacts(),
                    parsed.pointsInFavor(),
                    parsed.pointsAgainst()
            );

        } catch (Exception ex) {
            log.warn("Gemini API call failed for bill explanation. Activating fallback simulation: {}", ex.getMessage());
            return buildFallbackBillExplanation(request, authenticatedUserId);
        }
    }

    /**
     * Answers citizen questions about democracy, citizenship, public transparency, or course topics
     * using structured JSON output for dynamic suggested topics.
     */
    public AiTutorChatResponse askTutor(AiTutorChatRequest request, Long authenticatedUserId) {
        String ragContext = politicalTransparencyService != null
                ? politicalTransparencyService.buildRagContextForQuestion(request.question())
                : "";

        if (!isApiKeyConfigured()) {
            log.info("GEMINI_API_KEY not configured. Using structured fallback simulation for tutor query: {}", request.question());
            return buildFallbackTutorAnswer(request, authenticatedUserId, ragContext);
        }

        try {
            StringBuilder promptBuilder = new StringBuilder();
            if (ragContext != null && !ragContext.isBlank()) {
                promptBuilder.append("[Dados Oficiais da Plataforma:\n").append(ragContext).append("]\n\n");
            }
            if (request.lessonContext() != null && !request.lessonContext().isBlank()) {
                promptBuilder.append("[Contexto da Aula: ").append(request.lessonContext()).append("]\n\n");
            }
            promptBuilder.append("Pergunta do cidadão: ").append(request.question()).append("\n\n");
            promptBuilder.append("""
                    Responda EXCLUSIVAMENTE com um objeto JSON válido contendo exatamente as chaves:
                    {
                      "answer": "Resposta didática, apartidária e objetiva em português do Brasil",
                      "suggestedTopics": ["Tópico relacionado 1 para aprofundar", "Tópico relacionado 2", "Tópico relacionado 3"]
                    }
                    """);

            String rawJson = callGeminiStructuredJson(promptBuilder.toString(), TUTOR_SYSTEM_INSTRUCTION);
            if (rawJson == null || rawJson.isBlank()) {
                return buildFallbackTutorAnswer(request, authenticatedUserId, ragContext);
            }

            StructuredTutorReply parsed = parseTutorReply(rawJson);
            if (parsed == null || parsed.answer() == null || parsed.answer().isBlank()) {
                return buildFallbackTutorAnswer(request, authenticatedUserId, ragContext);
            }

            Long historyId = recordHistoryIfUserAuthenticated(
                    authenticatedUserId,
                    request.question(),
                    parsed.answer()
            );

            return AiTutorChatResponse.ofReal(
                    request.question(),
                    parsed.answer(),
                    request.subject() != null ? request.subject() : "Cidadania e Política",
                    parsed.suggestedTopics(),
                    historyId
            );

        } catch (Exception ex) {
            log.warn("Gemini API call failed for tutor. Activating fallback simulation: {}", ex.getMessage());
            return buildFallbackTutorAnswer(request, authenticatedUserId, ragContext);
        }
    }

    private String callGeminiStructuredJson(String userPrompt, String systemInstruction) {
        GeminiRequest payload = GeminiRequest.structuredJson(systemInstruction, userPrompt);

        GeminiResponse result = restClient.post()
                .uri("/models/{model}:generateContent", model)
                .body(payload)
                .retrieve()
                .body(GeminiResponse.class);

        return result != null ? result.extractText() : null;
    }

    private StructuredBillAnalysis parseBillAnalysis(String rawText) {
        try {
            JsonNode root = objectMapper.readTree(stripCodeFences(rawText));
            String simplified = root.path("simplifiedExplanation").asText(null);
            String impacts = root.path("practicalImpacts").asText(null);
            List<String> pros = extractStringList(root.path("pointsInFavor"));
            List<String> cons = extractStringList(root.path("pointsAgainst"));

            if (simplified == null || simplified.isBlank()) {
                return null;
            }
            if (impacts == null || impacts.isBlank()) {
                impacts = "Impacto direto na regulamentação e aplicação das diretrizes deste projeto de lei.";
            }
            if (pros.isEmpty()) {
                pros = List.of("Promove maior clareza normativa sobre o tema abordado no projeto.");
            }
            if (cons.isEmpty()) {
                cons = List.of("Exige acompanhamento da regulamentação e da viabilidade de implementação.");
            }
            return new StructuredBillAnalysis(simplified.trim(), impacts.trim(), pros, cons);
        } catch (Exception ex) {
            log.warn("Could not parse structured JSON for explainBill, falling back to plain text: {}", ex.getMessage());
            return new StructuredBillAnalysis(
                    rawText.trim(),
                    "Impacto direto na regulamentação e aplicação das diretrizes deste projeto de lei.",
                    List.of("Promove maior clareza normativa sobre o tema abordado no projeto."),
                    List.of("Exige acompanhamento da regulamentação e da viabilidade de implementação.")
            );
        }
    }

    private StructuredTutorReply parseTutorReply(String rawText) {
        try {
            JsonNode root = objectMapper.readTree(stripCodeFences(rawText));
            String answer = root.path("answer").asText(null);
            List<String> topics = extractStringList(root.path("suggestedTopics"));

            if (answer == null || answer.isBlank()) {
                return null;
            }
            if (topics.isEmpty()) {
                topics = List.of(
                        "Acompanhamento de proposições no Congresso Nacional",
                        "Controle social e transparência de recursos públicos"
                );
            }
            return new StructuredTutorReply(answer.trim(), topics);
        } catch (Exception ex) {
            log.warn("Could not parse structured JSON for askTutor, falling back to plain text: {}", ex.getMessage());
            return new StructuredTutorReply(
                    rawText.trim(),
                    List.of(
                            "Acompanhamento de proposições no Congresso Nacional",
                            "Controle social e transparência de recursos públicos"
                    )
            );
        }
    }

    private List<String> extractStringList(JsonNode arrayNode) {
        List<String> items = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            for (JsonNode item : arrayNode) {
                String value = item.asText("").trim();
                if (!value.isEmpty()) {
                    items.add(value);
                }
            }
        }
        return items;
    }

    private String stripCodeFences(String raw) {
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                return trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
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

    private AiTutorChatResponse buildFallbackTutorAnswer(AiTutorChatRequest request, Long authenticatedUserId, String ragContext) {
        StringBuilder answerBuilder = new StringBuilder();
        answerBuilder.append(String.format(
                "Olá! Sou o Tutor Cidadão Huari. Sobre sua dúvida (\"%s\"): "
                        + "No contexto da educação política e da cidadania brasileira, compreender o funcionamento dos órgãos públicos "
                        + "e a destinação dos recursos é fundamental. O poder público atua como representante do cidadão, e mecanismos de transparência "
                        + "permitem fiscalizar projetos de lei, orçamentos e a atuação de deputados e senadores para fortalecer a democracia.",
                request.question()
        ));

        if (ragContext != null && !ragContext.isBlank()) {
            answerBuilder.append("\n\n**Informações relevantes encontradas na base cívica da Huari:**\n");
            answerBuilder.append(ragContext);
        }

        String simulatedAnswer = answerBuilder.toString();

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

    private record StructuredBillAnalysis(
            String simplifiedExplanation,
            String practicalImpacts,
            List<String> pointsInFavor,
            List<String> pointsAgainst) {
    }

    private record StructuredTutorReply(
            String answer,
            List<String> suggestedTopics) {
    }
}

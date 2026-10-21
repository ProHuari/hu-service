package com.huariservice.huariia.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "true")
public class GeradorRespostaIaGemini implements GeradorRespostaIa {

    private static final String SISTEMA = """
            Você é o assistente da plataforma de cursos Huari.ia. \
            Ajude o aluno com dúvidas sobre os conteúdos de estudo, de forma clara e objetiva, \
            em português do Brasil. Se a pergunta fugir completamente de estudo e aprendizado, \
            diga educadamente que só pode ajudar com isso.""";

    private final RestClient client;
    private final String modelo;

    public GeradorRespostaIaGemini(RestClient iaRestClient,
                                   @Value("${app.ia.model:gemini-1.5-flash}") String modelo) {
        this.client = iaRestClient;
        this.modelo = modelo;
    }

    @Override
    public String gerarResposta(String pergunta) {
        try {
            Requisicao req = new Requisicao(
                    new ConteudoSistema(List.of(new Parte(SISTEMA))),
                    List.of(new Conteudo(List.of(new Parte(pergunta))))
            );

            Resposta resposta = client.post()
                    .uri("/v1beta/models/{modelo}:generateContent", modelo)
                    .body(req)
                    .retrieve()
                    .body(Resposta.class);

            if (resposta == null || resposta.candidates() == null || resposta.candidates().isEmpty()) {
                return null;
            }

            Candidate candidate = resposta.candidates().get(0);
            if (candidate.content() == null || candidate.content().parts() == null) {
                return null;
            }

            return candidate.content().parts().stream()
                    .map(Parte::text)
                    .filter(t -> t != null && !t.isBlank())
                    .reduce("", String::concat)
                    .trim();

        } catch (RestClientResponseException e) {
            log.error("Falha na API do Gemini: status {}", e.getStatusCode().value());
            int status = e.getStatusCode().value();
            if (status == 429) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "A IA está sobrecarregada ou atingiu o limite gratuito. Tente novamente em instantes.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível obter resposta da IA.");
        } catch (ResourceAccessException e) {
            log.error("Timeout ou falha de conexão com a API do Gemini: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT,
                    "A IA demorou demais para responder.");
        }
    }

    private record Parte(String text) {}

    private record Conteudo(List<Parte> parts) {}

    private record ConteudoSistema(@JsonProperty("parts") List<Parte> parts) {}

    private record Requisicao(
            @JsonProperty("systemInstruction") ConteudoSistema systemInstruction,
            List<Conteudo> contents) {}

    private record Candidate(Conteudo content) {}

    private record Resposta(List<Candidate> candidates) {}
}

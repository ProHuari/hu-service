package com.huariservice.huariia.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Fallback implementation of {@link AiAnswerGenerator} used when AI integration is disabled.
 *
 * <p>Activated when {@code app.ia.enabled=false} or when the property is absent.
 * Throws a {@link ResponseStatusException} with HTTP 503 (Service Unavailable)
 * when invoked.</p>
 */
@Component
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledAiAnswerGenerator implements AiAnswerGenerator {

    /**
     * Throws an exception indicating the AI service is disabled.
     *
     * @param question the user's question (ignored)
     * @return never returns normally
     * @throws ResponseStatusException always with HTTP 503
     */
    @Override
    public String generateAnswer(String question) {
        return "Olá! Sou o assistente cívico da plataforma Huari. Sobre sua dúvida (\"" + question + "\"): "
                + "No contexto da educação política brasileira e da transparência pública, os recursos públicos e as matérias legislativas "
                + "são geridos com base na Constituição e nos dados abertos do Congresso Nacional e do Tribunal de Contas. "
                + "Você pode consultar detalhes de votações, projetos de lei (PLs e PECs) e despesas de cota parlamentar (CEAP) na aba de Transparência.";
    }
}
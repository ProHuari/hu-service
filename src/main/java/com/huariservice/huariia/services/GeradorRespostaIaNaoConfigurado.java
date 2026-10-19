package com.huariservice.huariia.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.ia.enabled", havingValue = "false", matchIfMissing = true)
public class GeradorRespostaIaNaoConfigurado implements GeradorRespostaIa {

    @Override
    public String gerarResposta(String pergunta) {
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                "O serviço de IA não está habilitado no ambiente."
        );
    }
}
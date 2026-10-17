
package com.huariservice.huariia.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GeradorRespostaIaNaoConfigurado
        implements GeradorRespostaIa {

    @Override
    public String gerarResposta(String pergunta) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "A integração com a IA ainda não foi configurada."
        );
    }
}

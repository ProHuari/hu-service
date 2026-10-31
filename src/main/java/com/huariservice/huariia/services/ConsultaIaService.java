
package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.HistoricoIaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class ConsultaIaService {

    private final GeradorRespostaIa geradorRespostaIa;
    private final HistoricoIaService historicoIaService;


    public HistoricoIaResponse consultar(
            Long usuarioId,
            String pergunta) {

        String resposta = geradorRespostaIa.gerarResposta(pergunta);

        if (resposta == null || resposta.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "A IA não retornou uma resposta válida."
            );
        }

        return historicoIaService.registrarHistorico(
                usuarioId,
                pergunta,
                resposta
        );
    }
}

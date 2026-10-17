
package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.HistoricoIaResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ConsultaIaService {

    private final GeradorRespostaIa geradorRespostaIa;
    private final HistoricoIaService historicoIaService;

    public ConsultaIaService(
            GeradorRespostaIa geradorRespostaIa,
            HistoricoIaService historicoIaService) {
        this.geradorRespostaIa = geradorRespostaIa;
        this.historicoIaService = historicoIaService;
    }

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

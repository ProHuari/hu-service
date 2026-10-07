package com.huariservice.huariia.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(LocalDateTime timestamp,
                           int status,
                           String erro,
                           String mensagem,
                           List<String> detalhes) {

    public ErroResponse(int status, String erro, String mensagem) {
        this(LocalDateTime.now(), status, erro, mensagem, null);
    }

    public ErroResponse(int status, String erro, String mensagem, List<String> detalhes) {
        this(LocalDateTime.now(), status, erro, mensagem, detalhes);
    }
}
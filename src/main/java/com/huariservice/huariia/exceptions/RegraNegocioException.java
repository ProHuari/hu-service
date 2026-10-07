package com.huariservice.huariia.exceptions;

// 422: a requisição é válida, mas viola uma regra de negócio
// (ex: concluir aula sem estar matriculado no curso)
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
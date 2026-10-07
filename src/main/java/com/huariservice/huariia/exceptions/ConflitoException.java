package com.huariservice.huariia.exceptions;

// 409: recurso já existe (e-mail duplicado, matrícula repetida, ordem já usada...)
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
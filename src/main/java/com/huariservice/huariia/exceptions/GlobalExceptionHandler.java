package com.huariservice.huariia.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: service não achou o recurso (id do path ou id enviado no corpo)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
    }

    // 409: duplicidade detectada pelo service (existsBy...)
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> tratarConflito(ConflitoException ex) {
        return resposta(HttpStatus.CONFLICT, "Conflito", ex.getMessage());
    }

    // 409: última linha de defesa, quando o banco barra (unique, FK) e o service não pegou antes
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarIntegridade(DataIntegrityViolationException ex) {
        log.warn("Violação de integridade no banco: {}", ex.getMostSpecificCause().getMessage());
        return resposta(HttpStatus.CONFLICT, "Conflito",
                "A operação viola uma restrição de dados (registro duplicado ou em uso).");
    }

    // 422: regra de negócio
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException ex) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", ex.getMessage());
    }

    // 400: @Valid no corpo da requisição
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais campos estão inválidos",
                detalhes
        ));
    }

    // 400: @Validated em @PathVariable / @RequestParam
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> tratarConstraint(ConstraintViolationException ex) {
        List<String> detalhes = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        return ResponseEntity.badRequest().body(new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais parâmetros estão inválidos",
                detalhes
        ));
    }

    // 400: JSON malformado, enum inexistente, tipo errado no corpo
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarCorpoInvalido(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Corpo da requisição ausente, malformado ou com valores inválidos.");
    }

    // 400: /cursos/abc quando o id é Long
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarTipoParametro(MethodArgumentTypeMismatchException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Valor inválido para o parâmetro '" + ex.getName() + "'.");
    }

    // 400: parâmetro obrigatório ausente
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> tratarParametroAusente(MissingServletRequestParameterException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O parâmetro '" + ex.getParameterName() + "' é obrigatório.");
    }

    // 405: método HTTP errado
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido", ex.getMessage());
    }

    // 500: rede de segurança. SEMPRE loga o stack trace, senão o erro real some.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(Exception ex) {
        log.error("Erro inesperado", ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String erro, String mensagem) {
        return ResponseEntity.status(status).body(new ErroResponse(status.value(), erro, mensagem));
    }
}
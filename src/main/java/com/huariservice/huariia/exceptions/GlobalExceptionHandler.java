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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Translates exceptions thrown by controllers and services into a consistent
 * {@link ErrorResponse} payload with the appropriate HTTP status.
 * User-facing texts are kept in Portuguese.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404: the service could not find the resource (path id or id sent in the body).
     *
     * @param ex the exception
     * @return 404 response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
    }

    /**
     * 409: duplicate detected by the service (existsBy... checks).
     *
     * @param ex the exception
     * @return 409 response
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return response(HttpStatus.CONFLICT, "Conflito", ex.getMessage());
    }

    /**
     * 409: last line of defense, when the database rejects the write (unique, FK)
     * and the service did not catch it before.
     *
     * @param ex the exception
     * @return 409 response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Database integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return response(HttpStatus.CONFLICT, "Conflito",
                "A operação viola uma restrição de dados (registro duplicado ou em uso).");
    }

    /**
     * 422: business rule violation.
     *
     * @param ex the exception
     * @return 422 response
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
        return response(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", ex.getMessage());
    }

    /**
     * 400: {@code @Valid} failed on the request body.
     *
     * @param ex the exception
     * @return 400 response listing each invalid field
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais campos estão inválidos",
                details
        ));
    }

    /**
     * 400: {@code @Validated} failed on a {@code @PathVariable} / {@code @RequestParam}.
     *
     * @param ex the exception
     * @return 400 response listing each invalid parameter
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        return ResponseEntity.badRequest().body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Um ou mais parâmetros estão inválidos",
                details
        ));
    }

    /**
     * 400: malformed JSON, unknown enum value or wrong type in the body.
     *
     * @param ex the exception
     * @return 400 response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return response(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Corpo da requisição ausente, malformado ou com valores inválidos.");
    }

    /**
     * 400: path/query parameter with the wrong type (e.g. {@code /courses/abc} when the id is a Long).
     *
     * @param ex the exception
     * @return 400 response naming the offending parameter
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return response(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Valor inválido para o parâmetro '" + ex.getName() + "'.");
    }

    /**
     * 400: required request parameter is missing.
     *
     * @param ex the exception
     * @return 400 response naming the missing parameter
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return response(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O parâmetro '" + ex.getParameterName() + "' é obrigatório.");
    }

    /**
     * 405: wrong HTTP method for the route.
     *
     * @param ex the exception
     * @return 405 response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return response(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido", ex.getMessage());
    }

    /**
     * Propagates the status of a {@link ResponseStatusException} (used mainly by the AI integration).
     *
     * @param ex the exception
     * @return response with the exception's status code and reason
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();

        String message = ex.getReason() == null
                ? "A operação não pode ser concluída."
                : ex.getReason();

        String error = ex.getStatusCode().is5xxServerError()
                ? "Serviço indisponível"
                : "Requisição inválida";

        return ResponseEntity.status(status)
                .body(new ErrorResponse(status, error, message));
    }

    /**
     * 401: wrong e-mail or password on login.
     *
     * @param ex the exception
     * @return 401 response
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return response(HttpStatus.UNAUTHORIZED, "Não autorizado", ex.getMessage());
    }

    /**
     * 404: unknown route (without this handler it would fall into the generic handler and become a 500).
     *
     * @param ex the exception
     * @return 404 response
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return response(HttpStatus.NOT_FOUND, "Recurso não encontrado", "A rota solicitada não existe.");
    }

    /**
     * 500: safety net. ALWAYS logs the stack trace, otherwise the real error is lost.
     *
     * @param ex the exception
     * @return 500 response with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    /**
     * Builds an error response without details.
     *
     * @param status  HTTP status
     * @param error   short error title
     * @param message error description
     * @return the response entity
     */
    private ResponseEntity<ErrorResponse> response(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), error, message));
    }
}

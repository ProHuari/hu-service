package com.huariservice.huariia.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error payload returned by the API.
 *
 * @param timestamp when the error occurred
 * @param status    HTTP status code
 * @param error     short, user-facing error title (in Portuguese)
 * @param message   user-facing error description (in Portuguese)
 * @param details   optional list of field/parameter validation errors; omitted
 *                  when {@code null}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details) {

    /**
     * Creates an error payload without details, timestamped now.
     *
     * @param status  HTTP status code
     * @param error   short error title
     * @param message error description
     */
    public ErrorResponse(int status, String error, String message) {
        this(LocalDateTime.now(), status, error, message, null);
    }

    /**
     * Creates an error payload with validation details, timestamped now.
     *
     * @param status  HTTP status code
     * @param error   short error title
     * @param message error description
     * @param details list of validation errors
     */
    public ErrorResponse(int status, String error, String message, List<String> details) {
        this(LocalDateTime.now(), status, error, message, details);
    }
}

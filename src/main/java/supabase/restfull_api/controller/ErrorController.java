package supabase.restfull_api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import supabase.restfull_api.model.WebResponse;

import java.util.stream.Collectors;

/**
 * Global Controller Advice to handle validation and HTTP status exceptions across the API.
 */
@RestControllerAdvice
public class ErrorController {

    /**
     * Handles validation errors (ConstraintViolationException).
     * Returns 400 Bad Request with details.
     *
     * @param exception Validation exception
     * @return ResponseEntity with WebResponse containing errors message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<String>> handleConstraintViolationException(ConstraintViolationException exception) {
        String errorMessage = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder()
                        .errors(errorMessage)
                        .build());
    }

    /**
     * Handles response status exceptions (ResponseStatusException).
     * Returns corresponding HTTP status and message.
     *
     * @param exception HTTP status exception
     * @return ResponseEntity with WebResponse containing errors message
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> handleResponseStatusException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(WebResponse.<String>builder()
                        .errors(exception.getReason())
                        .build());
    }
}

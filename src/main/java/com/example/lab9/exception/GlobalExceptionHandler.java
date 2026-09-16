package com.example.lab9.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(AccountNotFoundException exception) {
        return buildError(HttpStatus.NOT_FOUND, Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler({DuplicateAccountNumberException.class, InvalidAmountException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException exception) {
        return buildError(HttpStatus.BAD_REQUEST, Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> details.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return buildError(HttpStatus.BAD_REQUEST, details);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, Map<String, String> details) {
        ApiError error = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), details);
        return ResponseEntity.status(status).body(error);
    }
}

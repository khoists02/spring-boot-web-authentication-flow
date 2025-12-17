package com.practice.service.api;

import com.practice.service.dto.ErrorResponse;
import com.practice.service.exceptions.BadRequestException;
import com.practice.service.exceptions.EmailAlreadyExistsException;
import com.practice.service.exceptions.RateLimitExceededException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<?> handleRateLimit(
            RateLimitExceededException ex
    ) {
        return ResponseEntity.status(429)
                .header("Retry-After", String.valueOf(ex.getRetryAfter()))
                .body(Map.of(
                        "error", "RATE_LIMIT_EXCEEDED",
                        "retryAfter", ex.getRetryAfter()
                ));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailExists(
            EmailAlreadyExistsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        error.put("timestamp", Instant.now());
        error.put("type", EmailAlreadyExistsException.class.getName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrorCode(ex.getErrorCode());

        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }

    // Handle all JPA exceptions
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setErrorCode("404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    // Handle all SQL exceptions
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(SQLException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setErrorCode("500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    // Handle Spring Data / JPA exceptions
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessException(DataAccessException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setTimestamp(Instant.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrorCode("400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Catch Hibernate SQLGrammarException
    @ExceptionHandler(SQLGrammarException.class)
    public ResponseEntity<String> handleSQLGrammarException(SQLGrammarException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("SQL Syntax Error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleBadRequest(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        if (ex.getMessage().equals("Access Denied")) {
            error.setMessage(ex.getMessage());
            error.setTimestamp(Instant.now());
            error.setStatus(HttpStatus.UNAUTHORIZED.value());
            error.setErrorCode("403");
        } else {
            error.setMessage(ex.getMessage());
            error.setTimestamp(Instant.now());
            error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            error.setErrorCode("500");
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}

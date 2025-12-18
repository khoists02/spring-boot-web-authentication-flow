/*
 * FuckUB Pty. Ltd. ("LKG") CONFIDENTIAL
 * Copyright (c) 2025 FuckUB project Pty. Ltd. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of LKG. The intellectual and technical concepts contained
 * herein are proprietary to LKG and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from LKG.  Access to the source code contained herein is hereby forbidden to anyone except current LKG employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 */
package com.practice.service.api;

import com.practice.service.dto.ErrorResponse;
import com.practice.service.exceptions.BadRequestException;
import com.practice.service.exceptions.EmailAlreadyExistsException;
import com.practice.service.exceptions.RateLimitExceededException;
import com.practice.service.exceptions.JwtAuthenticationException;
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
    // ================= Helper method =================
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message, String errorCode, HttpStatus status
    ) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(message);
        error.setTimestamp(Instant.now());
        error.setStatus(status.value());
        error.setErrorCode(errorCode);
        return ResponseEntity.status(status).body(error);
    }

    // ================= Specific Exceptions =================
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException ex) {
        return ResponseEntity.status(429)
                .header("Retry-After", String.valueOf(ex.getRetryAfter()))
                .body(Map.of(
                        "error", "RATE_LIMIT_EXCEEDED",
                        "retryAfter", ex.getRetryAfter()
                ));
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> handleUnAuthentication(JwtAuthenticationException ex) {
        return buildErrorResponse(
                ex.getMessage(),
                ex.getErrorCode(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), "EMAIL_EXISTS", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return buildErrorResponse(ex.getMessage(), ex.getErrorCode(), ex.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), "404", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({SQLException.class, SQLGrammarException.class})
    public ResponseEntity<ErrorResponse> handleSQLExceptions(Exception ex) {
        return buildErrorResponse(
                ex.getMessage(),
                "500",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        return buildErrorResponse(rootMessage, "400", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        if ("Access Denied".equals(ex.getMessage())) {
            return buildErrorResponse(ex.getMessage(), "403", HttpStatus.UNAUTHORIZED);
        }
        return buildErrorResponse(ex.getMessage(), "500", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

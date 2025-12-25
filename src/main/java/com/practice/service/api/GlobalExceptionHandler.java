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

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.practice.service.dto.ErrorResponse;
import com.practice.service.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Map<String, String> TOKEN_ERROR_MAP = Map.of(
            "EXPIRED_TOKEN", "1004",
            "INVALID_TOKEN", "1003",
            "REFRESH_TOKEN_EXPIRED", "1002",
            "UNAUTHENTICATED", "1000",
            "INVALID_CREDENTIALS", "1001"
    );
    // ================= Helper method =================
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message, String errorCode, HttpStatus status
    ) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(message);
        error.setTimestamp(Instant.now());
        error.setStatus(status.value());
        if (StringUtils.isNotBlank(errorCode)) {
            error.setErrorCode(errorCode);
        }
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
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handlenotFoundError(NoHandlerFoundException ex) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return buildErrorResponse("Not Found", "404", HttpStatus.NOT_FOUND);
        }
        return buildErrorResponse("UNAUTHENTICAED", "1000", HttpStatus.UNAUTHORIZED);
    }

    // Unauthentication all resouces.
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<?> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return buildErrorResponse("UNAUTHENTICAED", "10000", HttpStatus.UNAUTHORIZED);
    }

    // Access Deniend
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> deniedException(AuthorizationDeniedException ex) {
        return buildErrorResponse("ACESS_DENIED", "", HttpStatus.FORBIDDEN);
    }

    // Access Deniend
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> deniedException(NoResourceFoundException ex) {
        return buildErrorResponse(ex.getMessage(), "404", HttpStatus.NOT_FOUND);
    }

    // Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        String errorMessage;
        String errorCode;
        HttpStatus status;

        switch (ex.getMessage()) {
            case "EXPIRED_TOKEN" -> {
                errorMessage = ex.getMessage();
                errorCode = TOKEN_ERROR_MAP.get("EXPIRED_TOKEN");
                status = HttpStatus.UNAUTHORIZED;
            }
            case "INVALID_TOKEN", "REFRESH_TOKEN_EXPIRED", "UNAUTHENTICATED" -> {
                errorMessage = ex.getMessage();
                errorCode = TOKEN_ERROR_MAP.get(ex.getMessage());
                status = HttpStatus.UNAUTHORIZED;
            }
            default -> {
                errorCode = "";
                errorMessage = ex.getMessage();
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return buildErrorResponse(errorMessage, errorCode, status);
    }
}

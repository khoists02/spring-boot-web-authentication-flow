package com.practice.service.exceptions;

import org.springframework.http.HttpStatus;

public class UnAuthenticationException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public UnAuthenticationException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
        this.errorCode = "unauthorized";
    }

    public UnAuthenticationException(String message, String errorCode) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

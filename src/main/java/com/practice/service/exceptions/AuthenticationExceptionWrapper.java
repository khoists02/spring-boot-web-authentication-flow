package com.practice.service.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationExceptionWrapper extends AuthenticationException {
    private final String code;
    public AuthenticationExceptionWrapper(String cause, String code) {
        super(cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

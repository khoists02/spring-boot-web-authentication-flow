package com.practice.service.api.auth.manager;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationExceptionWrapper extends AuthenticationException {
    AuthenticationExceptionWrapper(Throwable cause) {
        super("", cause);
    }
}

package com.practice.service.api.auth.manager;

import com.practice.service.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

public class MethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private final AuthenticationService authenticationService;
    private final HttpServletRequest request;

    public MethodSecurityExpressionRoot(Supplier<? extends @Nullable Authentication> authentication, AuthenticationService authenticationService, HttpServletRequest request) {
        super(authentication);
        this.authenticationService = authenticationService;
        this.request = request;
    }

    public boolean hasPermission(String permission) {
        return authenticationService.hasPermission(permission);
    }

    @Override
    public void setFilterObject(Object filterObject) {

    }

    @Override
    public @Nullable Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(@Nullable Object returnObject) {

    }

    @Override
    public @Nullable Object getReturnObject() {
        return null;
    }

    @Override
    public @Nullable Object getThis() {
        return null;
    }
}

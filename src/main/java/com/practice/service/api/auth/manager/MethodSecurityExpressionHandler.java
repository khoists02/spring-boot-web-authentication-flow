package com.practice.service.api.auth.manager;

import com.practice.service.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

public class MethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private final AuthenticationService authenticationService;
    private final HttpServletRequest request;

    public MethodSecurityExpressionHandler(AuthenticationService authenticationService, HttpServletRequest request) {
        this.authenticationService = authenticationService;
        this.request = request;
    }

    @Override
    public EvaluationContext createEvaluationContext(Supplier<? extends @Nullable Authentication> authentication, MethodInvocation mi) {
        StandardEvaluationContext standardEvaluationContext = (StandardEvaluationContext) super.createEvaluationContext(authentication, mi);

        MethodSecurityExpressionRoot root = new MethodSecurityExpressionRoot(authentication, authenticationService, request);
        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setRoleHierarchy(this.getRoleHierarchy());
        standardEvaluationContext.setRootObject(root);
        return  standardEvaluationContext;

    }
}

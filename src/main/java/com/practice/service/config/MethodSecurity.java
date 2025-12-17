package com.practice.service.config;

import com.practice.service.repositories.UserRepository;
import com.practice.service.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurity {
    private static final Logger logger = LoggerFactory.getLogger(MethodSecurity.class);

    private final AuthenticationService authenticationService;
    private final HttpServletRequest request;

    public MethodSecurity(HttpServletRequest request, AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.request = request;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(){
        return new com.practice.service.api.auth.manager.MethodSecurityExpressionHandler(authenticationService, request);
    }
}

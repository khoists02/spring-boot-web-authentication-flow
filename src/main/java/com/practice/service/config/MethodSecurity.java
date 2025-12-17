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

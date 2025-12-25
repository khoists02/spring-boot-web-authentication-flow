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

import com.practice.service.api.auth.manager.UserAccessDeniedHandler;
import com.practice.service.api.auth.manager.UserAuthenticationEntryPoint;
import com.practice.service.api.filter.FilterChainExceptionHanlder;
import com.practice.service.api.filter.JwtAuthenticationFilter;
import com.practice.service.api.filter.LogContextFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig {
    private final UserAccessDeniedHandler accessDeniedHandler;
    private final UserAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtFilter;
    private final FilterChainExceptionHanlder filterChainExceptionHanlder;
    private final LogContextFilter logContextFilter;

    public SecurityConfig(LogContextFilter logContextFilter, FilterChainExceptionHanlder filterChainExceptionHanlder, UserAccessDeniedHandler accessDeniedHandler, UserAuthenticationEntryPoint authenticationEntryPoint, JwtAuthenticationFilter jwtFilter) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtFilter = jwtFilter;
        this.filterChainExceptionHanlder = filterChainExceptionHanlder;
        this.logContextFilter = logContextFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/register/**", "/public/**", "/websockets/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(logContextFilter, AuthorizationFilter.class)
                .addFilterBefore(jwtFilter, AuthorizationFilter.class)
                .addFilterAfter(filterChainExceptionHanlder, ExceptionTranslationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}

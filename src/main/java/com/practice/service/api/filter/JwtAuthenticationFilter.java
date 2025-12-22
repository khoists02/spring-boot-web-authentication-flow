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
package com.practice.service.api.filter;

import com.practice.service.api.auth.manager.AppUserDetails;
import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.JwtAuthenticationException;
import com.practice.service.repositories.PermissionRepository;
import com.practice.service.repositories.UserRepository;
import com.practice.service.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtAuthenticationFilter(PermissionRepository permissionRepository, JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // /auth/ why it has slash, causing api authenticated.
        return request.getServletPath().startsWith("/auth/") || request.getServletPath().startsWith("/register") || request.getServletPath().startsWith("/public");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
        logger.info("JwtAuthenticationFilter");
        String accessToken = resolvedAccessToken(request);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            jwtUtil.parser().parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(
                    "EXPIRED_TOKEN"
            );
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(
                    "INVALID_TOKEN"
            );
        }
        try {
            String username = jwtUtil.getUsername(accessToken);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new JwtAuthenticationException("UNAUTHENTICATED"));

            Set<GrantedAuthority> authorities =
                    permissionRepository.findAllPermissionsByUserId(user.getId())
                            .stream()
                            .map(p -> new SimpleGrantedAuthority(p.getName()))
                            .collect(Collectors.toSet());

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            new AppUserDetails(user, authorities),
                            null,
                            authorities
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("JwtAuthenticationFilter", e);
        } finally {
            logger.info("JwtAuthenticationFilter complete");
        }

    }

    private String resolvedAccessToken(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("jwt.token")) token = c.getValue();
            }
        }
        return token;
    }
}

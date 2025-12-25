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
package com.practice.service.services;

import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.JwtAuthenticationException;
import com.practice.service.repositories.PermissionRepository;
import com.practice.service.repositories.UserRepository;
import com.practice.service.utils.JwtUtil;
import com.practice.service.utils.UserUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private HttpServletResponse response;
    private final PermissionRepository permissionRepository;

    public AuthenticationService (PermissionRepository permissionRepository, HttpServletResponse response, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.response = response;
        this.permissionRepository = permissionRepository;
    }

    @Transactional
    public void authenticate(String username, String password) {
        if (!userRepository.existsByEmail(username))  {
            throw new JwtAuthenticationException("UNAUTHENTICATED");
        }
        User user = userRepository.findByEmail(username).orElseThrow(() -> new JwtAuthenticationException("UNAUTHENTICATED"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new JwtAuthenticationException("INVALID_CREDENTIALS");
        }
        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        injectAccessTokenToCookie(token, response);
        injectRefreshTokenToCookie(refreshToken, response);
    }

    public void logout(HttpServletResponse response) {
        injectAccessTokenToCookie("", response);
        injectRefreshTokenToCookie("", response);
    }


    private void injectAccessTokenToCookie(String token, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt.token", token)
                .httpOnly(true)
                .secure(true)                 // 🔥 BẮT BUỘC cho SameSite=None
                .sameSite("None")             // 🔥 để gửi được qua WSS
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void injectRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt.refresh", refreshToken)
                .httpOnly(true)
                .secure(true)                 // 🔥 BẮT BUỘC cho SameSite=None
                .sameSite("None")             // 🔥 để gửi được qua WSS
                .path("/")
                .maxAge(Duration.ofMinutes(60))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = resolvedToken(request, "refresh");
        if (token == null) {
            throw new JwtAuthenticationException("INVALID_TOKEN"); // revoled user.
        }
        Claims claims;
        try {
            claims = jwtUtil.parser().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("REFRESH_TOKEN_EXPIRED"); // revoled user.
        } catch (JwtException e) {
            throw new JwtAuthenticationException("INVALID_TOKEN"); // revoled user.
        }
        User user = userRepository.getReferenceById(UUID.fromString(claims.get("usr").toString()));
        String newAccessToken = jwtUtil.generateToken(user);
        injectAccessTokenToCookie(newAccessToken, response);
    }

    public boolean hasPermission(String permission) {
        return permissionRepository.userHasPermission(UserUtil.getCurrentUserDetails().getUserId(), permission);
    }

    private String resolvedToken(HttpServletRequest request, String type) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("jwt." + type)) token = c.getValue();
            }
        }
        return token;
    }
}

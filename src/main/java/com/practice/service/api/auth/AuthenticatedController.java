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
package com.practice.service.api.auth;
import com.practice.service.dto.AuthenticationResponse;
import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.JwtAuthenticationException;
import com.practice.service.repositories.UserRepository;
import com.practice.service.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authenticatedUser")
public class AuthenticatedController {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(AuthenticatedController.class);

    public AuthenticatedController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAuthenticatedUser(){
        List<String> authorizeNames = UserUtil.getAuthorities();
        String email = UserUtil.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new JwtAuthenticationException("Unauthenticated", "11000"));
        return ResponseEntity.ok(mapToResponse(user, authorizeNames));
    }

    private AuthenticationResponse mapToResponse(User user, List<String> permissions) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setId(user.getId().toString());
        response.setPermissions(permissions);
        return response;
    }
}

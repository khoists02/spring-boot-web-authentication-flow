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

import com.practice.service.dto.AuthenticationRequest;
import com.practice.service.dto.ResponseMessage;
import com.practice.service.exceptions.JwtAuthenticationException;
import com.practice.service.services.AuthenticationService;
import com.practice.service.support.Audit;
import com.practice.service.support.RateLimit;
import com.practice.service.support.RateLimitType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public  AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @RateLimit(
            key = "login",
            limit = 15,
            duration = 60,
            type = RateLimitType.IP
    )
    @Audit(action = "login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationService.authenticate(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("success"));
        } catch (Exception ex) {
            throw new JwtAuthenticationException(ex.getMessage());
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authenticationService.logout(response);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Success"));
    }

    @PutMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request,HttpServletResponse response) {
        authenticationService.refreshToken(request,response);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Refresh Token success"));
    }
}

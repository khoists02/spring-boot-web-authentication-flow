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

import com.practice.service.dto.RegistrationModel;
import com.practice.service.services.RegistrationService;
import com.practice.service.support.RateLimit;
import com.practice.service.support.RateLimitType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    // ADD RATE LIMIT TO AVOID ATTACH SPAM EMAIL AND REGISTER.
    @RateLimit(
            key = "register",
            limit = 5,
            duration = 60,
            type = RateLimitType.IP
    )
    public ResponseEntity register(@Valid @RequestBody RegistrationModel model) throws Exception {
        registrationService.registerNewUser(model);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        registrationService.verifyToken(token);
        return ResponseEntity.ok("Email verified successfully.");
    }

}

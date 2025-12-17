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

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EmailTokenService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder encoder =
            Base64.getUrlEncoder().withoutPadding();

    public String generatePlainToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }

    public String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }
}

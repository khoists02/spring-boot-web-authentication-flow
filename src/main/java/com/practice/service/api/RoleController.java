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
package com.practice.service.api;

import com.practice.service.dto.RoleResponse;
import com.practice.service.entities.auth.Role;
import com.practice.service.repositories.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public ResponseEntity<?> getRoles() {
        List<RoleResponse> roles = roleRepository.findAll()
                .stream()
                .map(role -> {
                    // map Role entity -> RoleResponse DTO
                    RoleResponse dto = new RoleResponse();
                    dto.setId(role.getId().toString());
                    dto.setCode(role.getName());
                    dto.setDescription(role.getDescription());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(roles);
    }
}

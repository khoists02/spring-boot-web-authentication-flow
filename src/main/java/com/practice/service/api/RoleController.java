package com.practice.service.api;

import com.practice.service.entities.auth.Permission;
import com.practice.service.entities.auth.Roles;
import com.practice.service.repositories.PermissionRepository;
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
        List<Roles> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }
}

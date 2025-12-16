package com.practice.service.api.auth;

import com.practice.service.dto.AuthenticationResponse;
import com.practice.service.entities.User;
import com.practice.service.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/authenticatedUser")
public class AuthenticatedController {
    private final UserRepository userRepository;

    public AuthenticatedController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new JwtException("Authentication is null");
        }

        List<String> roleNames = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new JwtException("User not found"));
        return ResponseEntity.ok(mapToResponse(user, roleNames));
    }

    private AuthenticationResponse mapToResponse(User user, List<String> roles) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setId(user.getId().toString());
        response.setRoles(roles);
        return response;
    }
}

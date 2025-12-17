package com.practice.service.api.auth;
import com.practice.service.dto.AuthenticationResponse;
import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.UnAuthenticationException;
import com.practice.service.repositories.UserRepository;
import com.practice.service.utils.UserUtil;
import io.jsonwebtoken.JwtException;
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

    public AuthenticatedController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAuthenticatedUser(){
        List<String> authorizeNames = UserUtil.getAuthorities();
        String email = UserUtil.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UnAuthenticationException("User not found"));
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

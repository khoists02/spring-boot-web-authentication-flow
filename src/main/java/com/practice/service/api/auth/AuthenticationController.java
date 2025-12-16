package com.practice.service.api.auth;

import com.practice.service.dto.AuthenticationRequest;
import com.practice.service.dto.AuthenticationResponse;
import com.practice.service.entities.User;
import com.practice.service.services.AuthenticationService;
import com.practice.service.support.RateLimit;
import com.practice.service.support.RateLimitType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    public  AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @RateLimit(
            key = "login",
            limit = 15,
            duration = 60,
            type = RateLimitType.IP
    )
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        // get and checking user has "exist" and matching with password
        User user = authenticationService.getUserMatching(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(user));
    }

    private AuthenticationResponse mapToResponse(User user) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setId(user.getId().toString());
        return response;
    }
}

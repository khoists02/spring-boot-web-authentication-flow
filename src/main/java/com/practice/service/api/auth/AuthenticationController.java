package com.practice.service.api.auth;

import com.practice.service.dto.AuthenticationRequest;
import com.practice.service.dto.AuthenticationResponse;
import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.UnAuthenticationException;
import com.practice.service.services.AuthenticationService;
import com.practice.service.support.RateLimit;
import com.practice.service.support.RateLimitType;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
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
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        try {
            authenticationService.authenticate(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        } catch (Exception ex) {
            throw new UnAuthenticationException(ex.getMessage());
        }
    }
}

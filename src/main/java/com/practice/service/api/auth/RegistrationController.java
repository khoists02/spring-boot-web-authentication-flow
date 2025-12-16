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
@RequestMapping("/auth")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
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

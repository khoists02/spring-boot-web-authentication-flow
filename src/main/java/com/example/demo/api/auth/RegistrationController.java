package com.example.demo.api.auth;

import com.example.demo.dto.RegistrationModel;
import com.example.demo.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
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

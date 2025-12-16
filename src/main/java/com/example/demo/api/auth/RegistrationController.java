package com.example.demo.api.auth;

import com.example.demo.dto.RegistrationModel;
import com.example.demo.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity register(@Valid @RequestBody RegistrationModel model) {
        registrationService.registerNewUser(model);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

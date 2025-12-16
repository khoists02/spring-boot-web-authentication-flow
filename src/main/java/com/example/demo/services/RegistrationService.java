package com.example.demo.services;

import com.example.demo.dto.RegistrationModel;
import com.example.demo.entities.Registration;
import com.example.demo.repositories.RegistrationRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(RegistrationRepository registrationRepository, PasswordEncoder passwordEncoder) {
        this.registrationRepository = registrationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerNewUser(RegistrationModel model) {
        registrationRepository.save(mapToEntity(model));
    }

    private Registration mapToEntity(RegistrationModel model) {
        Registration registration = new Registration();
        registration.setEmail(model.getEmail());
        registration.setPassword(passwordEncoder.encode(model.getPassword()));
        registration.setUsername(model.getFirstName() + " " +  model.getLastName());
        return registration;
    }
}

package com.example.demo.services;

import com.example.demo.dto.EmailEvent;
import com.example.demo.dto.RegistrationModel;
import com.example.demo.entities.Registration;
import com.example.demo.exceptions.EmailAlreadyExistsException;
import com.example.demo.repositories.RegistrationRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailProducer emailProducer;
    private final JavaMailSender mailSender;

    public RegistrationService(RegistrationRepository registrationRepository, PasswordEncoder passwordEncoder, EmailProducer emailProducer, JavaMailSender  mailSender) {
        this.registrationRepository = registrationRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailProducer = emailProducer;
        this.mailSender = mailSender;
    }

    public void registerNewUser(RegistrationModel model) throws Exception {
        boolean exists = registrationRepository.existsByEmail(model.getEmail());
        if (exists) {
            throw new EmailAlreadyExistsException(model.getEmail());
        }
        registrationRepository.save(mapToEntity(model));

        // send email
        EmailEvent event = new EmailEvent();
        event.setTo(model.getEmail());
        event.setSubject("Registration Successful");
        event.setTemplate("register-success");

        Map<String, Object> vars = new HashMap<>();
        vars.put("username", model.getLastName());
        vars.put("email", model.getEmail());

        event.setVariables(vars);

        emailProducer.sendEmail(event);

    }

    private Registration mapToEntity(RegistrationModel model) {
        Registration registration = new Registration();
        registration.setEmail(model.getEmail());
        registration.setPassword(passwordEncoder.encode(model.getPassword()));
        registration.setUsername(model.getFirstName() + " " +  model.getLastName());

        return registration;
    }
}

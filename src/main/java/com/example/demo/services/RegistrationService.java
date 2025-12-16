package com.example.demo.services;

import com.example.demo.dto.EmailEvent;
import com.example.demo.dto.RegistrationModel;
import com.example.demo.entities.EmailVerificationToken;
import com.example.demo.entities.Registration;
import com.example.demo.entities.User;
import com.example.demo.exceptions.EmailAlreadyExistsException;
import com.example.demo.repositories.EmailVerificationTokenRepository;
import com.example.demo.repositories.RegistrationRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class RegistrationService {
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailProducer emailProducer;
    private final EmailTokenService emailTokenService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    public RegistrationService(UserRepository userRepository, EmailVerificationTokenRepository emailVerificationTokenRepository, EmailTokenService emailTokenService, RegistrationRepository registrationRepository, PasswordEncoder passwordEncoder, EmailProducer emailProducer, JavaMailSender mailSender) {
        this.registrationRepository = registrationRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailProducer = emailProducer;
        this.emailTokenService = emailTokenService;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
    }

    public void registerNewUser(RegistrationModel model) throws Exception {
        boolean exists = registrationRepository.existsByEmail(model.getEmail());
        if (exists) {
            throw new EmailAlreadyExistsException(model.getEmail());
        }
        registrationRepository.save(mapToEntity(model));

        // create a token
        String plainToken = emailTokenService.generatePlainToken();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setEmail(model.getEmail());
        token.setTokenHash(emailTokenService.hashToken(plainToken));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        emailVerificationTokenRepository.save(token);

        // send email
        EmailEvent event = new EmailEvent();
        event.setTo(model.getEmail());
        event.setSubject("Registration Successful");
        event.setTemplate("register-success");

        String verifyUrl = "http://localhost:8080/auth/verify?token=" + plainToken;

        Map<String, Object> vars = new HashMap<>();
        vars.put("username", model.getLastName());
        vars.put("email", model.getEmail());
        vars.put("verifyUrl", verifyUrl);
        event.setVariables(vars);
        emailProducer.sendEmail(event);

    }

    private Registration mapToEntity(RegistrationModel model) {
        Registration registration = new Registration();
        registration.setEmail(model.getEmail());
        registration.setPassword(passwordEncoder.encode(model.getPassword()));
        registration.setUsername(model.getFirstName() + " " + model.getLastName());

        return registration;
    }

    public void verifyToken(String token) {
        String tokenHash = emailTokenService.hashToken(token);

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        // find with decoded token.
        Registration registration = registrationRepository.findByEmail(verificationToken.getEmail()).orElseThrow(() -> new RuntimeException("Not found email"));

        if (registration != null) {
            User user = new User();
            user.setEmail(registration.getEmail());
            user.setPassword(registration.getPassword());
            user.setUsername(registration.getUsername());

            userRepository.save(user); // save user
            registrationRepository.delete(registration); // delete registration.
            emailVerificationTokenRepository.delete(verificationToken); // delete after used.
        }
    }


}

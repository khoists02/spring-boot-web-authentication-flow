package com.practice.service.services;

import com.practice.service.dto.EmailEvent;
import com.practice.service.dto.RegistrationModel;
import com.practice.service.entities.EmailVerificationToken;
import com.practice.service.entities.Registration;
import com.practice.service.entities.User;
import com.practice.service.exceptions.*;
import com.practice.service.repositories.EmailVerificationTokenRepository;
import com.practice.service.repositories.RegistrationRepository;
import com.practice.service.repositories.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
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

    @Transactional
    public void registerNewUser(RegistrationModel model) throws Exception {
        String email = model.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        if (registrationRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        registrationRepository.save(mapToEntity(model));

        if (emailVerificationTokenRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email); // if email verification aready exist, rollback all transition.
        }
        // create a token
        String plainToken = emailTokenService.generatePlainToken();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setEmail(model.getEmail());
        token.setTokenHash(emailTokenService.hashToken(plainToken));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        emailVerificationTokenRepository.save(token);
        // send email
        sendEmail(model, plainToken);
    }

    private void sendEmail(RegistrationModel model, String plainToken) {
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

    // This function to verify token from email
    // 1. Convert plain token to hash token
    // 2. find verify email with hash token
    // 3. if it exist => create new user => delete registration item and verify token email item.
    @Transactional
    public void verifyToken(String token) {
        // get hash token from plain token send by email
        String tokenHash = emailTokenService.hashToken(token);
        // find with hash token
        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() -> new InvalidTokenException("Invalid Hash Token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired");
        }
        Registration registration = registrationRepository
                .findByEmail(verificationToken.getEmail()).orElseThrow(() -> new NotFoundEmailException("Not found email"));

        boolean existUser = userRepository.existsByEmail(registration.getEmail());

        if (existUser) {
            throw new EmailAlreadyExistsException("User Email already exists"); // rollback
        }

        userRepository.save(mapRegistrationToUser(registration)); // save user
        registrationRepository.delete(registration); // delete registration.
        emailVerificationTokenRepository.delete(verificationToken); // delete after used.
    }

    // mappers.
    private Registration mapToEntity(RegistrationModel model) {
        Registration registration = new Registration();
        registration.setEmail(model.getEmail());
        registration.setPassword(passwordEncoder.encode(model.getPassword()));
        registration.setUsername(model.getFirstName() + " " + model.getLastName());

        return registration;
    }

    private User mapRegistrationToUser(Registration registration) {
        User user = new User();
        user.setEmail(registration.getEmail());
        user.setPassword(registration.getPassword());
        user.setUsername(registration.getUsername());
        return user;
    }

}

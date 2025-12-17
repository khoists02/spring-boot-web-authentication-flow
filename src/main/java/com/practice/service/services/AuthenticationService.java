package com.practice.service.services;

import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.BadRequestException;
import com.practice.service.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService (UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User getUserMatching(String username, String password) {
        if (!userRepository.existsByEmail(username))  {
            throw new UsernameNotFoundException(username);
        }
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Password do not match");
        }
        return user;
    }
}

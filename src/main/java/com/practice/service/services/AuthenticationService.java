package com.practice.service.services;

import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.UnAuthenticationException;
import com.practice.service.repositories.PermissionRepository;
import com.practice.service.repositories.UserRepository;
import com.practice.service.utils.JwtUtil;
import com.practice.service.utils.UserUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private HttpServletResponse response;
    private final PermissionRepository permissionRepository;

    public AuthenticationService (PermissionRepository permissionRepository, HttpServletResponse response, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.response = response;
        this.permissionRepository = permissionRepository;
    }

    @Transactional
    public void authenticate(String username, String password) {
        if (!userRepository.existsByEmail(username))  {
            throw new UnAuthenticationException("Username not found");
        }
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UnAuthenticationException("User not found: " + username));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnAuthenticationException("Password do not match");
        }
        String token = jwtUtil.generateToken(user);
        injectCookie(token, response);
    }


    private void injectCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 1 hour
        response.addCookie(cookie);
    }

    public boolean hasPermission(String permission) {
        return permissionRepository.userHasPermission(UserUtil.getCurrentUserDetails().getUserId(), permission);
    }
}

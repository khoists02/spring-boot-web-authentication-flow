package com.practice.service.api.auth;

import com.practice.service.dto.AuthenticationRequest;
import com.practice.service.dto.AuthenticationResponse;
import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.BadRequestException;
import com.practice.service.services.AuthenticationService;
import com.practice.service.support.RateLimit;
import com.practice.service.support.RateLimitType;
import com.practice.service.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;

    public  AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
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
            // 1️⃣ Kiểm tra user và password
            User user = authenticationService.getUserMatching(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );

            // 2️⃣ Authenticate với Spring Security
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );

            // 3️⃣ Generate JWT và inject cookie
            String token = jwtUtil.generateToken(auth);
            injectCookie(token, response);

            return ResponseEntity.ok(mapToResponse(user));

        } catch (UsernameNotFoundException ex) {
            // user không tồn tại
            throw new BadRequestException(ex.getMessage());

        } catch (BadRequestException ex) {
            // password không khớp hoặc custom business exception
            throw new BadRequestException(ex.getMessage());

        } catch (Exception ex) {
            // các lỗi khác
            throw new BadRequestException(ex.getMessage());
        }
    }

    private void injectCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 1 hour
        response.addCookie(cookie);
    }

    private AuthenticationResponse mapToResponse(User user) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setId(user.getId().toString());
        return response;
    }
}

package com.practice.service.api.auth.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.service.exceptions.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String errorCode = "401"; // default unauthorized
        String message = authException.getMessage();

        if (authException instanceof JwtAuthenticationException jwtEx) {
            errorCode = jwtEx.getErrorCode();
            message = jwtEx.getMessage();
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
        {
          "errorCode": "%s",
          "message": "%s"
        }
    """.formatted(errorCode, message));
    }
}

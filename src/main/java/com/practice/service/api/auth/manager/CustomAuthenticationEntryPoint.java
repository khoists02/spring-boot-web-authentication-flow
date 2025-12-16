package com.practice.service.api.auth.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = Map.of(
                "status", 401,
                "error", "UNAUTHORIZED",
                "message", authException.getMessage(),
                "path", request.getRequestURI()
        );

        String json = objectMapper.writeValueAsString(body);

        response.setContentLength(json.getBytes().length);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}

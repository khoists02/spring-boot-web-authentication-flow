package com.practice.service.api.auth.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // set header JSON
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> body = Map.of(
                "status", 403,
                "error", "FORBIDDEN",
                "message", accessDeniedException.getMessage(),
                "path", request.getRequestURI()
        );

        String json = objectMapper.writeValueAsString(body);

        // commit response đúng cách
        response.setContentLength(json.getBytes().length);
        response.getWriter().write(json);
        response.getWriter().flush();  // << quan trọng
    }
}

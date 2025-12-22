package com.practice.service.api.auth.manager;

import com.practice.service.exceptions.AuthenticationExceptionWrapper;
import com.practice.service.exceptions.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final Logger logger = LoggerFactory.getLogger(UserAuthenticationEntryPoint.class);

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
//        logger.error("ENTRYPOINT EX = {}", ex.getClass().getName());
//        String code = "10000";
//        String message = "UNAUTHENTICATED";
//
//        if (ex instanceof AuthenticationExceptionWrapper) {
//            message = ex.getCause().getMessage();
//        }
//
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//
//        response.getWriter().write("""
//        {
//          "errorCode": "%s",
//          "message": "%s"
//        }
//    """.formatted(code, message));
        if (ex instanceof AuthenticationExceptionWrapper) {
            handlerExceptionResolver.resolveException(request, response, null,(Exception) ex.getCause());
        } else {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

        try {
            response.flushBuffer();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}

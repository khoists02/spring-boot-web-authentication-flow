package com.practice.service.support;

import com.practice.service.entities.AuditLog;
import com.practice.service.repositories.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Aspect
@Component
public class AuditAspect {
    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AuditAspect(HttpServletRequest request, AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
        this.request = request;
    }

    private String getClientIp(HttpServletRequest request) {

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim(); // client IP đầu tiên
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    @Around("@annotation(audit)")
    public Object audit(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        String ip = getClientIp(request);
        String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElse("Anonymous");

        AuditLog auditLog = new AuditLog();
        auditLog.setIpAddress(ip);
        auditLog.setUsername(username);
        auditLog.setAction(audit.action());
        auditLog.setResource(joinPoint.getSignature().toShortString());

        try {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed(); // 👉 method chạy ở đây
            auditLog.setStatus("SUCCESS");
            auditLogRepository.save(auditLog);
            logger.info("Audit took {} ms", System.currentTimeMillis() - start);
            return result;
        } catch (Exception ex) {
            auditLog.setStatus("FAILED");
            auditLog.setError(ex.getMessage());
            auditLogRepository.save(auditLog);
            throw ex;
        }
    }
}

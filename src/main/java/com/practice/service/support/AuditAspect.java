package com.practice.service.support;

import com.practice.service.entities.AuditLog;
import com.practice.service.repositories.AuditLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Aspect
@Component
public class AuditAspect {
    private final AuditLogRepository auditLogRepository;
    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Around("@annotation(audit)")
    public Object audit(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {

        String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElse("Anonymous");

        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(username);
        auditLog.setAction(audit.action());
        auditLog.setResource(joinPoint.getSignature().toShortString());

        try {
            Object result = joinPoint.proceed(); // 👉 method chạy ở đây
            auditLog.setStatus("SUCCESS");
            auditLogRepository.save(auditLog);
            return result;
        } catch (Exception ex) {
            auditLog.setStatus("FAILED");
            auditLog.setError(ex.getMessage());
            auditLogRepository.save(auditLog);
            throw ex;
        }
    }
}

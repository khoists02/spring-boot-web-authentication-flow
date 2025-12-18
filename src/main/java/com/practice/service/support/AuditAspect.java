/*
 * FuckUB Pty. Ltd. ("LKG") CONFIDENTIAL
 * Copyright (c) 2025 FuckUB project Pty. Ltd. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of LKG. The intellectual and technical concepts contained
 * herein are proprietary to LKG and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from LKG.  Access to the source code contained herein is hereby forbidden to anyone except current LKG employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 */
package com.practice.service.support;

import com.practice.service.entities.AuditLog;
import com.practice.service.services.AuditService;
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
    private final AuditService auditService;
    private final HttpServletRequest request;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AuditAspect(AuditService auditService, HttpServletRequest request) {
        this.auditService = auditService;
        this.request = request;
    }

    @Around("@annotation(audit)")
    public Object audit(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {

        long start = System.currentTimeMillis();

        AuditLog log = buildBaseAuditLog(joinPoint, audit);

        try {
            Object result = joinPoint.proceed();

            log.setStatus("SUCCESS");
            auditService.save(log);

            logger.debug("Audit success [{}] took {} ms",
                    log.getAction(),
                    System.currentTimeMillis() - start);

            return result;

        } catch (Exception ex) {
            log.setStatus("FAILED");
            log.setError(ex.getMessage());

            auditService.save(log);
            throw ex;
        }
    }

    private AuditLog buildBaseAuditLog(
            ProceedingJoinPoint joinPoint,
            Audit audit) {

        AuditLog log = new AuditLog();
        log.setIpAddress(getClientIp(request));
        log.setUsername(getUsername());
        log.setAction(audit.action());
        log.setResource(joinPoint.getSignature().toShortString());

        return log;
    }

    private String getUsername() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElse("Anonymous");
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        return xRealIp != null ? xRealIp : request.getRemoteAddr();
    }
}

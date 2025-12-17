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

import com.practice.service.dto.RegistrationModel;
import com.practice.service.exceptions.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final StringRedisTemplate redis;
    private final HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object handleRateLimit(
            ProceedingJoinPoint joinPoint,
            RateLimit rateLimit
    ) throws Throwable {

        String key = buildKey(rateLimit, joinPoint);
        int limit = rateLimit.limit();
        int duration = rateLimit.duration();

        Long count = redis.opsForValue().increment(key);

        if (count != null && count == 1) {
            redis.expire(key, Duration.ofSeconds(duration));
        }

        if (count != null && count > limit) {
            throw new RateLimitExceededException(duration);
        }

        return joinPoint.proceed();
    }

    private String buildKey(
            RateLimit rateLimit,
            ProceedingJoinPoint joinPoint
    ) {

        return switch (rateLimit.type()) {
            case IP -> "rl:ip:" + request.getRemoteAddr();
            case EMAIL -> "rl:email:" + extractEmail(joinPoint);
            case GLOBAL -> "rl:global";
        };
    }

    private String extractEmail(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof RegistrationModel req) {
                return req.getEmail();
            }
        }
        return "unknown";
    }
}

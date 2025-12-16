package com.practice.service.exceptions;

public class RateLimitExceededException extends RuntimeException {
    private final int retryAfter;

    public RateLimitExceededException(int retryAfter) {
        super("Rate limit exceeded");
        this.retryAfter = retryAfter;
    }

    public int getRetryAfter() {
        return retryAfter;
    }
}

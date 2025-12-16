package com.practice.service.exceptions;

public class TokenExpiredException extends BadRequestException {
    public TokenExpiredException(String message) {
        super(message, "11003");
    }
}

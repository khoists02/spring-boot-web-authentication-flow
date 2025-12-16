package com.practice.service.exceptions;

public class InvalidTokenException extends BadRequestException {
    public InvalidTokenException(String message) {
        super(message, "11002");
    }
}

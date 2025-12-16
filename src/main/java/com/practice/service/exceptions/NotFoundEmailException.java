package com.practice.service.exceptions;

public class NotFoundEmailException extends BadRequestException {
    public NotFoundEmailException(String message) {
        super(message, "11004");
    }
}

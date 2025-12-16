package com.practice.service.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ErrorResponse {
    private String errorCode;
    private String message;
    private int status;
    private Instant timestamp;
}

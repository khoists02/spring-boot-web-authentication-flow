package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
public class ErrorResponse {
    private String errorCode;
    private String message;
    private int status;
    private Instant timestamp;
}

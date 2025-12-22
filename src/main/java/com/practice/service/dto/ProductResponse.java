package com.practice.service.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}

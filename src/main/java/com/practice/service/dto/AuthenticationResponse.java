package com.practice.service.dto;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String id;
    private String username;
    private String email;
}

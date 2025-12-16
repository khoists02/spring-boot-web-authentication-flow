package com.practice.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthenticationResponse {
    private String id;
    private String username;
    private String email;
    private List<String> roles;
}

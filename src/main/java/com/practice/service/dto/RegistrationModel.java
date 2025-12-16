package com.practice.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationModel {
    private String firstName;
    @NotBlank(message = "Last name should not be blank")
    @Size(min = 3, max = 20)
    private String lastName;

    @Email(message = "Invalid Email")
    @NotBlank(message = "Email should not be blank")
    private String email;

    @NotBlank(message = "Passowrd should not be blank")
    private String password;
}

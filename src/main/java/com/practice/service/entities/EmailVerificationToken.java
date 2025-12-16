package com.practice.service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_token")
@Getter
@Setter
public class EmailVerificationToken extends BaseEntity {
    private String tokenHash;
    private String email;
    private LocalDateTime expiresAt;
    private boolean verified = false;
}

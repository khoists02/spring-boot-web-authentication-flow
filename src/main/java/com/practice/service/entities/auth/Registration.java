package com.practice.service.entities.auth;

import com.practice.service.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "registrations")
@Getter
@Setter
public class Registration extends BaseEntity {
    private String username;
    private String password;
    private String email;
}

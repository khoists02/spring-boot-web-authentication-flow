package com.practice.service.entities.auth;

import com.practice.service.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Roles extends BaseEntity {
    private String name;
    private String description;
}

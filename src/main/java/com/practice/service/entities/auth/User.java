package com.practice.service.entities.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.service.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private boolean owner;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade =  {CascadeType.ALL},
            orphanRemoval = true
    )
    private Set<UserAndRole> userRoles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

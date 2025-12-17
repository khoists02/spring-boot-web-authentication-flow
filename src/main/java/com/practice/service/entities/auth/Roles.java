package com.practice.service.entities.auth;

import com.practice.service.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Roles extends BaseEntity {
    private String name;
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<RoleAndPermission> rolePermissions = new HashSet<>();

    public Set<Permission> getPermissionsFromRole(Roles role) {
        return role.getRolePermissions().stream()
                .map(RoleAndPermission::getPermission)
                .collect(Collectors.toSet());
    }
}

package com.practice.service.entities.auth;

import com.practice.service.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "roles_permissions")
@Getter
@Setter
public class RoleAndPermission extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleAndPermission that)) return false;
        return Objects.equals(role.getId(), that.role.getId())
                && Objects.equals(permission.getId(), that.permission.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(role.getId(), permission.getId());
    }
}

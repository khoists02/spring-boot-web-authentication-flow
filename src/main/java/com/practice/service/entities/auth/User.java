package com.practice.service.entities.auth;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserAndRole> userRoles = new HashSet<>();
//    public Set<Permission> getPermissionsFromUser(User user) {
//        return user.getUserRoles().stream()
//                .map(UserAndRole::getRole)
//                .flatMap(role ->
//                        role.getRolePermissions().stream()
//                                .map(RoleAndPermission::getPermission)
//                )
//                .collect(Collectors.toSet());
//    }
//
//    public List<SimpleGrantedAuthority> mapToAuthorities(User user) {
//        return getPermissionsFromUser(user).stream()
//                .map(p -> new SimpleGrantedAuthority(p.getName()))
//                .toList();
//    }
}

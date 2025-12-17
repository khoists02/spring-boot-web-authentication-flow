package com.practice.service.api.auth.manager;

import com.practice.service.entities.auth.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class AppUserDetails implements UserDetails {
    @Getter
    private final User user;
    private final Collection<GrantedAuthority> authorities;

    public AppUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = (Collection<GrantedAuthority>) authorities;
    }

    public UUID getUserId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // encoded password
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // hoặc email nếu muốn login bằng email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

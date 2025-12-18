package com.practice.service.api.auth.manager;

import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.JwtAuthenticationException;
import com.practice.service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new JwtAuthenticationException("Unauthenticated", "11000"));

        return new AppUserDetails(user, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}

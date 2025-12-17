package com.practice.service.api.filter;

import com.practice.service.api.auth.manager.AppUserDetails;
import com.practice.service.entities.auth.Permission;
import com.practice.service.entities.auth.User;
import com.practice.service.exceptions.BadRequestException;
import com.practice.service.repositories.PermissionRepository;
import com.practice.service.repositories.UserRepository;
import com.practice.service.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public JwtAuthenticationFilter(PermissionRepository permissionRepository, JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/login") || request.getServletPath().startsWith("/register");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
       try {
           String token = detectToken(request);

           if (token != null && jwtUtil.validateToken(token)) {
               String username = jwtUtil.getUsername(token);
               User user = userRepository.findByEmail(username).orElseThrow(() -> new BadRequestException("User not found"));

               Set<Permission> permissionSet = permissionRepository.findAllPermissionsByUserId(user.getId());
               Set<GrantedAuthority> authorities =
                       permissionSet.stream()
                               .map(p -> new SimpleGrantedAuthority(p.getName()))
                               .collect(Collectors.toSet());
               AppUserDetails userDetails = new AppUserDetails(user, authorities);
               Authentication authentication =
                       new UsernamePasswordAuthenticationToken(
                               userDetails,          // principal
                               null,                 // credentials
                               userDetails.getAuthorities()
                       );
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
           filterChain.doFilter(request, response);
       } catch (Exception e) {
           SecurityContextHolder.clearContext();
           filterChain.doFilter(request, response);
       }
    }

    private String detectToken(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("JWT")) token = c.getValue();
            }
        }
        return token;
    }
}

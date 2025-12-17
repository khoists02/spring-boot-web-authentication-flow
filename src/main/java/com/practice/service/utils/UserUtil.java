package com.practice.service.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class UserUtil {

    private UserUtil() {} // class static, không cần khởi tạo

    /**
     * Lấy Authentication hiện tại từ SecurityContext
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Lấy username của user đang login
     */
    public static String getUsername() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }

    /**
     * Lấy toàn bộ UserDetails nếu bạn dùng custom UserDetails (UserPrincipal)
     */
    public static UserDetails getCurrentUserDetails() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        return null;
    }

    /**
     * Lấy toàn bộ UserDetails nếu bạn dùng custom UserDetails (UserPrincipal)
     */
    public static List<String> getAuthorities() {
        Authentication auth = getAuthentication();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}

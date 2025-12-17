/*
 * FuckUB Pty. Ltd. ("LKG") CONFIDENTIAL
 * Copyright (c) 2025 FuckUB project Pty. Ltd. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of LKG. The intellectual and technical concepts contained
 * herein are proprietary to LKG and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from LKG.  Access to the source code contained herein is hereby forbidden to anyone except current LKG employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 */
package com.practice.service.utils;

import com.practice.service.api.auth.manager.AppUserDetails;
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
        if (principal instanceof AppUserDetails) {
            return ((AppUserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }

    /**
     * Lấy toàn bộ UserDetails nếu bạn dùng custom UserDetails (UserPrincipal)
     */
    public static AppUserDetails getCurrentUserDetails() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof AppUserDetails) {
            return (AppUserDetails) principal;
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

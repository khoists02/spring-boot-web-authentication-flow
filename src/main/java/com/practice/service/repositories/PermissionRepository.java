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
package com.practice.service.repositories;

import com.practice.service.entities.auth.Permission;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    @Query("""
        select distinct p
        from User u
        join u.userRoles ur
        join ur.role r
        join RoleAndPermission rp on rp.role = r
        join rp.permission p
        where u.id = :userId
    """)
    Set<Permission> findAllPermissionsByUserId(@Param("userId") UUID userId);

    @Query("""
    select count(p) > 0
    from User u
    join u.userRoles ur
    join ur.role r
    join RoleAndPermission rp on rp.role = r
    join rp.permission p
    where u.id = :userId
      and p.name = :permission
""")
    boolean userHasPermission(
            @Param("userId") UUID userId,
            @Param("permission") String permission
    );
}

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

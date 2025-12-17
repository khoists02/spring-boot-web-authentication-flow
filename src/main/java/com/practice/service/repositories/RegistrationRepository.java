package com.practice.service.repositories;

import com.practice.service.entities.auth.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    boolean existsByEmail(String email);

    Optional<Registration> findByEmail(String email);
}

package com.urlshortener.admin.repository;

import com.urlshortener.admin.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByAdminId(String adminId);

    Optional<AdminUser> findByAdminNo(Long adminNo);
}

package com.urlshortener.admin.service;

import com.urlshortener.admin.entity.AdminUser;
import com.urlshortener.admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;

    public Optional<AdminUser> findByAdminId(String adminId) {
        return adminUserRepository.findByAdminId(adminId);
    }

    public Optional<AdminUser> findByAdminNo(Long adminNo){
        return adminUserRepository.findByAdminNo(adminNo);
    }
}
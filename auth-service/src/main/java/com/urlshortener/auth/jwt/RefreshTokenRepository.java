package com.urlshortener.auth.jwt;

import com.urlshortener.auth.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserNoAndUserType(Long userNo, Role userType);

    Optional<RefreshToken> findByUserId(String userId);
}

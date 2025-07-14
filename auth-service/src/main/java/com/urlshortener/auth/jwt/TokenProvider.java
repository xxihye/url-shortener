package com.urlshortener.auth.jwt;


import com.urlshortener.auth.dto.JwtToken;
import com.urlshortener.common.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {
    @Value("{jwt.secret}")
    private String secretKeyString;

    @Value("{jwt.access-expiration}")
    private long accessTokenExpirationTime;

    @Value("{jwt.refresh-expiration}")
    private long refreshTokenExpirationTime;

    private Key secretKey;

    private static final String AUTHORITIES_KEY = "role";

    @PostConstruct
    public void init(){
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public JwtToken createToken(Long userNo, Role role) {
        String accessToken = createAccessToken(userNo, role);
        String refreshToken = createRefreshToken(userNo, role);
        return new JwtToken(accessToken, refreshToken, "Bearer", accessTokenExpirationTime / 1000);
    }

    private String createAccessToken(Long userNo, Role role) {
        return Jwts.builder()
                   .setSubject(String.valueOf(userNo))
                   .claim(AUTHORITIES_KEY, role.name())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }

    private String createRefreshToken(Long userNo, Role role) {
        return Jwts.builder()
                   .setSubject(String.valueOf(userNo))
                   .claim(AUTHORITIES_KEY, role.name())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }
}
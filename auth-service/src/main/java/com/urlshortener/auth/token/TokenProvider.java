package com.urlshortener.auth.token;

import com.urlshortener.auth.dto.JwtToken;
import com.urlshortener.auth.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    public static final String GRANT_TYPE = "Bearer";

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public JwtToken createUserToken(Long userNo) {
        return createToken(userNo, Role.ROLE_USER);
    }

    public JwtToken createAdminToken(Long adminNo) {
        return createToken(adminNo, Role.ROLE_ADMIN);
    }

    private JwtToken createToken(Long accountNo, Role role) {
        String accessToken = createAccessToken(accountNo, role);
        String refreshToken = createRefreshToken(accountNo, role);

        return JwtToken.builder()
                       .grantType(GRANT_TYPE)
                       .accessToken(accessToken)
                       .refreshToken(refreshToken)
                       .expiresIn(accessTokenExpirationTime / 1000)
                       .build();
    }

    private String createAccessToken(Long accountNo, Role role) {
        return Jwts.builder()
                   .setSubject(String.valueOf(accountNo))
                   .claim(AUTHORITIES_KEY, role.name())
                   .claim("token_type", "access")
                   .setId(UUID.randomUUID().toString())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }

    private String createRefreshToken(Long accountNo, Role role) {
        return Jwts.builder()
                   .setSubject(String.valueOf(accountNo))
                   .claim(AUTHORITIES_KEY, role.name())
                   .claim("token_type", "refresh")
                   .setId(UUID.randomUUID().toString())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String userId = claims.getSubject();

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(claims.get(AUTHORITIES_KEY, String.class)));

        UserDetails userDetails = new User(userId, "", authorities);

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public Long getAccountNo(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public String getRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get(AUTHORITIES_KEY, String.class);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(secretKey)
                       .build()
                       .parseClaimsJws(token)
                       .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
package com.urlshortener.auth.jwt;

import com.urlshortener.auth.dto.JwtToken;
import com.urlshortener.auth.enums.Role;
import com.urlshortener.auth.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TokenProvider {
    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-expiration}")
    private Long accessTokenExpirationTime;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpirationTime;

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

    private JwtToken createToken(Long userNo, Role role) {
        String accessToken = createAccessToken(userNo, role);
        String refreshToken = createRefreshToken(userNo, role);

        return JwtToken.builder()
                       .grantType(GRANT_TYPE)
                       .accessToken(accessToken)
                       .refreshToken(refreshToken)
                       .expiresIn(accessTokenExpirationTime / 1000)
                       .build();
    }

    private String createAccessToken(Long userNo, Role role) {
        return Jwts.builder()
                   .setSubject(String.valueOf(userNo))
                   .claim(AUTHORITIES_KEY, role.name())
                   .claim("token_type", "access")
                   .setId(UUID.randomUUID()
                              .toString())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }

    private String createRefreshToken(Long userNo, Role role) {
        return Jwts.builder()
                   .setSubject(String.valueOf(userNo))
                   .claim(AUTHORITIES_KEY, role.name())
                   .claim("token_type", "refresh")
                   .setId(UUID.randomUUID()
                              .toString())
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
        Long userNo = Long.parseLong(claims.getSubject());
        String role = claims.get(AUTHORITIES_KEY, String.class);
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        UserPrincipal principal = UserPrincipal.builder()
                                               .userNo(userNo)
                                               .password("")
                                               .role(Role.valueOf(role))
                                               .build();

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Long getAccountNo(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public LocalDateTime extractRefreshTokenExpiration(String refreshToken) {
        Claims claims = parseClaims(refreshToken);

        return claims.getExpiration()
                     .toInstant()
                     .atZone(ZoneId.of("Asia/Seoul"))
                     .toLocalDateTime();
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
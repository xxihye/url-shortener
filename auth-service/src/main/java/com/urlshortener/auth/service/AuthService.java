package com.urlshortener.auth.service;

import com.urlshortener.admin.entity.AdminUser;
import com.urlshortener.admin.service.AdminUserService;
import com.urlshortener.auth.dto.AuthRes;
import com.urlshortener.auth.dto.JwtToken;
import com.urlshortener.auth.dto.LoginReq;
import com.urlshortener.auth.dto.SignupReq;
import com.urlshortener.auth.enums.Role;
import com.urlshortener.auth.jwt.RefreshToken;
import com.urlshortener.auth.jwt.RefreshTokenRepository;
import com.urlshortener.auth.jwt.TokenProvider;
import com.urlshortener.exception.AdminNotFoundException;
import com.urlshortener.exception.DuplicateUserException;
import com.urlshortener.exception.InvalidPasswordException;
import com.urlshortener.exception.InvalidTokenException;
import com.urlshortener.exception.UserNotFoundException;
import com.urlshortener.user.entity.User;
import com.urlshortener.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //유저 회원가입
    public void signup(SignupReq req) {
        if (userService.existsByUserId(req.getUserId())) {
            throw new DuplicateUserException();
        }

        String encodedPw = passwordEncoder.encode(req.getPassword());

        userService.createUser(req.getUserId(), encodedPw);
    }

    //유저 로그인
    public AuthRes login(LoginReq req) {
        //유저 조회
        User user = userService.findByUserId(req.getUserId())
                               .orElseThrow(UserNotFoundException::new);

        //비밀번호 미일치
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        // Access + Refresh Token 생성
        JwtToken token = tokenProvider.createUserToken(user.getUserNo());
        LocalDateTime expiresIn = tokenProvider.extractRefreshTokenExpiration(token.getRefreshToken());

        // Refresh Token DB 저장
        refreshTokenRepository.findByUserNoAndUserType(user.getUserNo(), Role.ROLE_USER)
                              .ifPresentOrElse(
                                  rt -> {
                                      rt.updateToken(token.getRefreshToken(), expiresIn);
                                      refreshTokenRepository.save(rt);
                                  },
                                  () -> refreshTokenRepository.save(RefreshToken.builder()
                                                                                .userNo(user.getUserNo())
                                                                                .userId(user.getUserId())
                                                                                .userType(Role.ROLE_USER)
                                                                                .token(token.getRefreshToken())
                                                                                .issuedAt(LocalDateTime.now())
                                                                                .expiresAt(expiresIn)
                                                                                .build())
                              );

        // 응답 반환
        return AuthRes.builder()
                      .token(token)
                      .build();
    }

    //유저 토큰 재발급
    public AuthRes reissue(String refreshToken) {
        //토큰 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        Long userNo = tokenProvider.getAccountNo(refreshToken);

        //기존 refresh 토큰 조회
        RefreshToken savedToken = refreshTokenRepository.findByUserNoAndUserType(userNo, Role.ROLE_USER)
                                                        .orElseThrow(InvalidTokenException::new);

        if (!savedToken.getToken().equals(refreshToken)) {
            log.warn("Refresh token mismatch for user: {}", savedToken.getUserId());
            throw new InvalidTokenException();
        }

        //토큰 업데이트
        JwtToken newToken = tokenProvider.createUserToken(userNo);
        LocalDateTime expiresAt = tokenProvider.extractRefreshTokenExpiration(newToken.getRefreshToken());
        savedToken.updateToken(newToken.getRefreshToken(), expiresAt);

        refreshTokenRepository.save(savedToken);

        return AuthRes.builder()
                      .token(newToken)
                      .build();
    }

    //어드민 로그인
    public AuthRes adminLogin(LoginReq req) {
        //어드민 조회
        AdminUser admin = adminUserService.findByAdminId(req.getUserId())
                                          .orElseThrow(AdminNotFoundException::new);

        //비밀번호 미일치
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new InvalidPasswordException();
        }

        // Access + Refresh Token 생성
        JwtToken token = tokenProvider.createAdminToken(admin.getAdminNo());
        LocalDateTime expiresAt = tokenProvider.extractRefreshTokenExpiration(token.getRefreshToken());

        // Refresh Token DB 저장
        refreshTokenRepository.findByUserNoAndUserType(admin.getAdminNo(), Role.ROLE_ADMIN)
                              .ifPresentOrElse(
                                  rt -> {
                                      rt.updateToken(token.getRefreshToken(), expiresAt);
                                      refreshTokenRepository.save(rt);
                                  },
                                  () -> refreshTokenRepository.save(RefreshToken.builder()
                                                                                .userNo(admin.getAdminNo())
                                                                                .userId(admin.getAdminId())
                                                                                .userType(Role.ROLE_ADMIN)
                                                                                .token(token.getRefreshToken())
                                                                                .issuedAt(LocalDateTime.now())
                                                                                .expiresAt(expiresAt)
                                                                                .build())
                              );

        // 응답 반환
        return AuthRes.builder()
                      .token(token)
                      .build();

    }

    // 어드민 토큰 재발급
    public AuthRes reissueAdminToken(String refreshToken) {
        //토큰 검증
        if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        Long adminNo = tokenProvider.getAccountNo(refreshToken);

        AdminUser admin = adminUserService.findByAdminNo(adminNo)
                                          .orElseThrow(AdminNotFoundException::new);

        //기존 refresh 토큰 조회
        RefreshToken saved = refreshTokenRepository.findByUserId(admin.getAdminId())
                                                   .orElseThrow(InvalidTokenException::new);

        if (!saved.getToken().equals(refreshToken)) {
            throw new InvalidTokenException();
        }

        //토큰 생성 및 업데이트
        JwtToken newToken = tokenProvider.createAdminToken(adminNo);
        LocalDateTime expiresAt = tokenProvider.extractRefreshTokenExpiration(newToken.getRefreshToken());
        saved.updateToken(newToken.getRefreshToken(), expiresAt);

        refreshTokenRepository.save(saved);

        return AuthRes.builder()
                      .token(newToken)
                      .build();
    }
}

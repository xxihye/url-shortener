package com.urlshortener.auth.service;

import com.urlshortener.admin.entity.AdminUser;
import com.urlshortener.admin.service.AdminUserService;
import com.urlshortener.auth.dto.AuthRes;
import com.urlshortener.auth.dto.JwtToken;
import com.urlshortener.auth.dto.LoginReq;
import com.urlshortener.auth.dto.SignupReq;
import com.urlshortener.auth.token.RefreshToken;
import com.urlshortener.auth.token.RefreshTokenRepository;
import com.urlshortener.auth.token.TokenProvider;
import com.urlshortener.exception.AdminNotFoundException;
import com.urlshortener.exception.DuplicateUserException;
import com.urlshortener.exception.InvalidPasswordException;
import com.urlshortener.exception.InvalidTokenException;
import com.urlshortener.exception.UserNotFoundException;
import com.urlshortener.user.entity.User;
import com.urlshortener.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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

        // Refresh Token DB 저장
        refreshTokenRepository.findByUserNo(user.getUserNo())
                              .ifPresentOrElse(
                                  rt -> rt.updateToken(token.getRefreshToken()),
                                  () -> refreshTokenRepository.save(new RefreshToken(user.getUserId(), token.getRefreshToken()))
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

        User user = userService.findByUserNo(userNo).orElseThrow(UserNotFoundException::new);

        //기존 refresh 토큰 조회
        RefreshToken saved = refreshTokenRepository.findByUserId(user.getUserId())
                                                   .orElseThrow(InvalidTokenException::new);

        if (!saved.getToken().equals(refreshToken)) {
            throw new InvalidTokenException();
        }

        //토큰 생성 및 업데이트
        JwtToken newToken = tokenProvider.createUserToken(userNo);
        saved.updateToken(newToken.getRefreshToken());

        return AuthRes.builder()
                      .token(newToken)
                      .build();
    }

    //어드민 로그인
    public AuthRes adminLogin(LoginReq req) {
        //어드민 조회
        AdminUser admin = adminUserService.findByAdminId(req.getUserId()).orElseThrow(AdminNotFoundException::new);

        //비밀번호 미일치
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new InvalidPasswordException();
        }

        // Access + Refresh Token 생성
        JwtToken token = tokenProvider.createAdminToken(admin.getAdminNo());

        // Refresh Token DB 저장
        refreshTokenRepository.findByUserId(admin.getAdminId())
                              .ifPresentOrElse(
                                  rt -> rt.updateToken(token.getRefreshToken()),
                                  () -> refreshTokenRepository.save(new RefreshToken(admin.getAdminId(), token.getRefreshToken()))
                              );

        // 응답 반환
        return AuthRes.builder()
                      .token(token)
                      .build();

    }

    // 어드민 토큰 재발급
    public AuthRes reissueAdminToken(String refreshToken) {
        //토큰 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        Long adminNo = tokenProvider.getAccountNo(refreshToken);

        AdminUser admin = adminUserService.findByAdminNo(adminNo).orElseThrow(AdminNotFoundException::new);

        //기존 refresh 토큰 조회
        RefreshToken saved = refreshTokenRepository.findByUserId(admin.getAdminId())
                                                   .orElseThrow(InvalidTokenException::new);

        if (!saved.getToken().equals(refreshToken)) {
            throw new InvalidTokenException();
        }

        //토큰 생성 및 업데이트
        JwtToken newToken = tokenProvider.createAdminToken(adminNo);
        saved.updateToken(newToken.getRefreshToken());

        return AuthRes.builder()
                      .token(newToken)
                      .build();
    }
}

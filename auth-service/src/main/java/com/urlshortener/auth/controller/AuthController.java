package com.urlshortener.auth.controller;

import com.urlshortener.auth.dto.AuthRes;
import com.urlshortener.auth.dto.LoginReq;
import com.urlshortener.auth.dto.SignupReq;
import com.urlshortener.auth.jwt.JwtHeaderUtil;
import com.urlshortener.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(description = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReq req){
        authService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @Operation(description = "로그인")
    @PostMapping("/login")
    public ResponseEntity<AuthRes> login(@Valid @RequestBody LoginReq req){
        AuthRes res = authService.login(req);
        return ResponseEntity.ok(res);
    }

    @Operation(description = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<AuthRes> reissue(HttpServletRequest req){
        String refreshToken = JwtHeaderUtil.extractToken(req);

        return ResponseEntity.ok(authService.reissue(refreshToken));
    }

    @Operation(description = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req){
        String refreshToken = JwtHeaderUtil.extractToken(req);
        authService.logout(refreshToken);

        return ResponseEntity.ok().build();
    }

    @Operation(description = "어드민 로그인")
    @PostMapping("/admin/login")
    public ResponseEntity<AuthRes> adminLogin(@RequestBody LoginReq req) {
        AuthRes res = authService.adminLogin(req);
        return ResponseEntity.ok(res);
    }

    @Operation(description = "어드민 토큰 재발급")
    @PostMapping("/admin/reissue")
    public ResponseEntity<AuthRes> reissueAdminToken(HttpServletRequest req) {
        String refreshToken = JwtHeaderUtil.extractToken(req);
        return ResponseEntity.ok(authService.reissueAdminToken(refreshToken));
    }
}

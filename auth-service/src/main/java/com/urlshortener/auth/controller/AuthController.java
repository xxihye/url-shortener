package com.urlshortener.auth.controller;

import com.urlshortener.auth.dto.AuthRes;
import com.urlshortener.auth.dto.LoginReq;
import com.urlshortener.auth.dto.SignupReq;
import com.urlshortener.auth.jwt.JwtHeaderUtil;
import com.urlshortener.auth.service.AuthService;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReq req){
        authService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PostMapping("/login")
    public ResponseEntity<AuthRes> login(@Valid @RequestBody LoginReq req){
        AuthRes res = authService.login(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reissue")
    public ResponseEntity<AuthRes> reissue(HttpServletRequest req){
        String refreshToken = JwtHeaderUtil.extractToken(req);

        return ResponseEntity.ok(authService.reissue(refreshToken));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthRes> adminLogin(@RequestBody LoginReq req) {
        AuthRes res = authService.adminLogin(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/admin/reissue")
    public ResponseEntity<AuthRes> reissueAdminToken(HttpServletRequest req) {
        String refreshToken = JwtHeaderUtil.extractToken(req);
        return ResponseEntity.ok(authService.reissueAdminToken(refreshToken));
    }
}

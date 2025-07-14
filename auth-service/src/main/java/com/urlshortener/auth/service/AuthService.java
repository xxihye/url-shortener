package com.urlshortener.auth.service;

import com.urlshortener.auth.dto.JwtToken;
import com.urlshortener.auth.dto.LoginReq;
import com.urlshortener.auth.dto.LoginRes;
import com.urlshortener.auth.dto.SignupReq;
import com.urlshortener.auth.jwt.TokenProvider;
import com.urlshortener.common.Role;
import com.urlshortener.exception.DuplicateUserException;
import com.urlshortener.exception.InvalidPasswordException;
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
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    //회원가입
    public void signup(SignupReq req) {
        if(userService.existsByUserId(req.getUserId())){
            throw new DuplicateUserException();
        }

        String encodedPw = passwordEncoder.encode(req.getPassword());

        userService.createUser(req.getUserId(), encodedPw);
    }

    //로그인
    public LoginRes login(LoginReq req){
        User user = userService.findByUserId(req.getUserId())
            .orElseThrow(UserNotFoundException::new);

        //비밀번호 미일치
        if(!passwordEncoder.matches(req.getPassword(), user.getPassword())){
            throw new InvalidPasswordException();
        }

        JwtToken token = tokenProvider.createToken(user.getUserNo(), Role.ROLE_USER);
    }

}

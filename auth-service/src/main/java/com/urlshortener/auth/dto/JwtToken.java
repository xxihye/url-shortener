package com.urlshortener.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class JwtToken {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private long expiresIn;
}

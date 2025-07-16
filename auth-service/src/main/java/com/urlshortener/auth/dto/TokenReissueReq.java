package com.urlshortener.auth.dto;

import lombok.Getter;

@Getter
public class TokenReissueReq {
    private String refreshToken;
}
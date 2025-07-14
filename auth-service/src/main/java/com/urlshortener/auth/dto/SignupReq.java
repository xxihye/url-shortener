package com.urlshortener.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupReq {

    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}
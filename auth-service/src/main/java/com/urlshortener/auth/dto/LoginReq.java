package com.urlshortener.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginReq {
    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}

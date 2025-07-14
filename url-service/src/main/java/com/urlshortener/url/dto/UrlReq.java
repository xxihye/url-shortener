package com.urlshortener.url.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlReq {

    @NotBlank(message = "원본 URL은 필수입니다.")
    private String originalUrl;

    private LocalDateTime expirationDate;
}

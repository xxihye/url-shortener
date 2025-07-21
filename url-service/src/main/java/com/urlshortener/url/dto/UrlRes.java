package com.urlshortener.url.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UrlRes {
    private String shortUrl;
    private String originalUrl;
    private LocalDateTime expirationDate;
}

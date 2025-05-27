package com.urlshortener.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UrlRes {
    private String shortUrl;
    private LocalDateTime expirationDate;
}

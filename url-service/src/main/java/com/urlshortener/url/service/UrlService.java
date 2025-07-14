package com.urlshortener.url.service;

import com.urlshortener.config.AppProperties;
import com.urlshortener.url.domain.Url;
import com.urlshortener.url.dto.UrlReq;
import com.urlshortener.url.dto.UrlRes;
import com.urlshortener.exception.InvalidExpirationException;
import com.urlshortener.exception.InvalidUrlException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.url.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final AppProperties appProperties;

    public UrlRes createShortUrl(UrlReq req) {
        //만료기한 기본값 설정
        if (req.getExpirationDate() == null) {
            req.setExpirationDate(LocalDateTime.now()
                                               .plusDays(30));
        }

        //요청 유효성 검증
        validate(req);

        Url url = Url.builder()
                     .originalUrl(req.getOriginalUrl())
                     .expirationDate(req.getExpirationDate())
                     .build();

        url = urlRepository.save(url);

        // id 기반의 shortKey 지정
        url.setShortKey(encodeIdToBase64(url.getId()));
        urlRepository.saveAndFlush(url);

        return UrlRes.builder()
                     .shortUrl(appProperties.getDomain() + url.getShortKey())
                     .expirationDate(url.getExpirationDate())
                     .build();
    }

    private void validate(UrlReq req) {
        try {
            new URL(req.getOriginalUrl());

            if (isExpired(req.getExpirationDate())) {
                throw new InvalidExpirationException();
            }

        } catch (MalformedURLException e) {
            throw new InvalidUrlException();
        }
    }

    private boolean isExpired(LocalDateTime dateTime) {
        return LocalDateTime.now()
                            .isAfter(dateTime);
    }

    //id -> shortKey 변환
    private String encodeIdToBase64(long id) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(id);

        return Base64.getUrlEncoder()
                     .withoutPadding()
                     .encodeToString(buffer.array());
    }

    public String getOriginalUrl(String shortKey) {
        Url url = urlRepository.findByShortKey(shortKey).orElseThrow(UrlNotFoundException::new);

        if(isExpired(url.getExpirationDate())){
            throw new UrlNotFoundException();
        }

        return url.getOriginalUrl();
    }
}

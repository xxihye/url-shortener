package com.urlshortener.controller;

import com.urlshortener.dto.UrlReq;
import com.urlshortener.dto.UrlRes;
import com.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlRes> createShortUrl(@RequestBody @Valid UrlReq req){
        UrlRes res = urlService.createShortUrl(req);

        return ResponseEntity.ok(res);
    }
}

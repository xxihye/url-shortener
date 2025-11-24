package com.urlshortener.url.controller;

import com.urlshortener.security.UserPrincipal;
import com.urlshortener.url.dto.UrlReq;
import com.urlshortener.url.dto.UrlRes;
import com.urlshortener.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @Operation(description = "단축 url 생성")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/shorten")
    public ResponseEntity<UrlRes> createShortUrl(@RequestBody @Valid UrlReq req,
                                                 @AuthenticationPrincipal UserPrincipal userPrincipal){

        Long userNo = userPrincipal.getUserNo();
        return ResponseEntity.ok(urlService.createShortUrl(req, userNo));
    }

    @Operation(description = "단축 url 전체 조회")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/urls")
    public ResponseEntity<List<UrlRes>> getAllUrls(@AuthenticationPrincipal UserPrincipal userPrincipal){
        Long userNo = userPrincipal.getUserNo();

        return ResponseEntity.ok(urlService.getAllUrlsByUserNo(userNo));
    }
}

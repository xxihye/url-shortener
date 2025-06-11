package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class UrlNotFoundException extends BaseException {
    public UrlNotFoundException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.URL_NOT_FOUND);
    }
}

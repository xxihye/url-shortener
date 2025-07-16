package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class AdminNotFoundException extends BaseException{
    public AdminNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }
}

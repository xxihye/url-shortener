package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }
}
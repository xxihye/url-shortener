package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends BaseException{
    public UnauthorizedUserException() {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
    }
}
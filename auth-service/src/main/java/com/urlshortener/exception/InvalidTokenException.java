package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException{
    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN);
    }
}

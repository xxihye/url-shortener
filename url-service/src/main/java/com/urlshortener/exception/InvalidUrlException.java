package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class InvalidUrlException extends BaseException{
    public InvalidUrlException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_URL);
    }
}

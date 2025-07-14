package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BaseException {
    public InvalidPasswordException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PASSWORD);
    }
}

package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class InvalidExpirationException extends BaseException {
    public InvalidExpirationException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_EXPIRATION_DATE);
    }
}

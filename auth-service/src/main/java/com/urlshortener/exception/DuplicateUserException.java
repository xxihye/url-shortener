package com.urlshortener.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUserException extends BaseException {

    public DuplicateUserException() {
        super(HttpStatus.CONFLICT, ErrorCode.DUPLICATE_USER);
    }
}

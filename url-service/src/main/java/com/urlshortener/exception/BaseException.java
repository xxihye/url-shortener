package com.urlshortener.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException{

    private final HttpStatus status;
    private final ErrorCode errorCode;

    protected BaseException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = status;
        this.errorCode = errorCode;
    }
}

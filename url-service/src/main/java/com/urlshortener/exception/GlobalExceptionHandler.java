package com.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleErrors() {
        return ResponseEntity.internalServerError()
                             .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handErrors(BaseException ex) {
        return ResponseEntity.status(ex.getStatus())
                             .body(ErrorResponse.of(ex.getStatus(), ex.getErrorCode()));
    }

}

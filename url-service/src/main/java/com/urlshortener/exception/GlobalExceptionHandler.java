package com.urlshortener.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleErrors(HttpServletRequest req) {
        return ResponseEntity.internalServerError()
                             .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURI()));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handErrors(BaseException ex, HttpServletRequest req) {
        return ResponseEntity.status(ex.getStatus())
                             .body(ErrorResponse.of(ex.getStatus(), ex.getErrorCode(), req.getRequestURI()));
    }

}

package com.urlshortener.exception;


import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
@Getter
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public static ErrorResponse of(HttpStatus status, ErrorCode code, String path) {
        return ErrorResponse.builder()
                            .status(status.value())
                            .error(status.getReasonPhrase())
                            .message(code.getMessage())
                            .timestamp(LocalDateTime.now())
                            .path(path)
                            .build();
    }
}

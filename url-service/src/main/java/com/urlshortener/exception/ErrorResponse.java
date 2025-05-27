package com.urlshortener.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public static ErrorResponse of(HttpStatus status, ErrorCode errorCode) {
        return ErrorResponse.builder()
                            .status(status.value())
                            .error(status.getReasonPhrase())
                            .message(errorCode.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build();
    }
}

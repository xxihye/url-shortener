package com.urlshortener.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MISSING_PARAMETER("필수 요청 파라미터가 누락되었습니다."),
    INVALID_URL("유효하지 않은 URL입니다."),
    INVALID_EXPIRATION_DATE("만료기한은 현재보다 이전일 수 없습니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류입니다."),
    URL_NOT_FOUND("해당 단축 URL은 존재하지 않거나 만료되었습니다.");

    private final String message;
}

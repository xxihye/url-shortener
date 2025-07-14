package com.urlshortener.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_USER("이미 존재하는 사용자입니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD("비밀번호가 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류입니다.");

    private final String message;
}

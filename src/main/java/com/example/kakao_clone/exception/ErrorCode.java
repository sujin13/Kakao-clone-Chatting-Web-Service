package com.example.kakao_clone.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode{
    AUTHENTICATION_FAILED(401, "AUTHENTICATION-FAILED", "인증에 실패했습니다."),
    USER_NOT_FOUND_ERROR(400, "USER-NOT-FOUND", "사용자를 찾을 수 없습니다."),

    METHOD_ARGUMENT_TYPE_MISMATCHED(400, "METHOD-ARGUMENT-TYPE-MISMATCHED", "메소드 인자 타입이 일치하지 않습니다."),
    PARAMETER_BINDING_ERROR(400, "PARAMETER-BINDING-ERROR", "파라미터 바인딩에 실패하였습니다."),
    METHOD_NOT_ALLOWED(405, "METHOD-NOT-ALLOWED", "허용되지 않는 형식의 메소드입니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL-SERVER-ERROR", "내부 서버 오류가 발생하였습니다.");


    private final int status;
    private final String errorType;
    private final String message;
}
package com.example.kakao_clone.exception;

import lombok.Data;

@Data
public class ErrorResponse {

    private int status;
    private String errorType;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.errorType = errorCode.getErrorType();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus();
        this.errorType = errorCode.getErrorType();
        this.message = message;
    }

    @Override
    public String toString() {
        return status + " " + errorType + " " + message;
    }
}

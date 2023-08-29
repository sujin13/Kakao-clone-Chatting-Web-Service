package com.example.kakao_clone.global.auth.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {
    @Schema(description = "아이디", example = "userId")
    private String userId;

    @Schema(description = "password", example = "password")
    private String password;
}
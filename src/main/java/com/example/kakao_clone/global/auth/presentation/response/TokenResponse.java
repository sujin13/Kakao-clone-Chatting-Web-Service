package com.example.kakao_clone.global.auth.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;

    private String refreshToken;

    private String grantType;

    private Long expiresIn;
}
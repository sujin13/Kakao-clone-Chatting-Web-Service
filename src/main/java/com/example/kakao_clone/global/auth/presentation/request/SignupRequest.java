package com.example.kakao_clone.global.auth.presentation.request;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {
    private String userId;

    private String password;

    private String name;

    private String statusMessage;

    private String profileImageUrl;

    private String backgroundImageUrl;

    private LocalDateTime lastLoginDate;

    @Builder
    public static SignupRequest encodePassword(SignupRequest signupRequest, String encodedPassword) {
        return SignupRequest.builder()
                .userId(signupRequest.getUserId())
                .password(encodedPassword)
                .name(signupRequest.getName())
                .statusMessage(signupRequest.getStatusMessage())
                .profileImageUrl(signupRequest.getProfileImageUrl())
                .backgroundImageUrl(signupRequest.getBackgroundImageUrl())
                .lastLoginDate(signupRequest.getLastLoginDate())
                .build();
    }
}

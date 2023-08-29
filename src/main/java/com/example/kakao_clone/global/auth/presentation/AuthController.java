package com.example.kakao_clone.global.auth.presentation;


import com.example.kakao_clone.feature.user.application.UserService;
import com.example.kakao_clone.global.auth.application.AuthService;
import com.example.kakao_clone.global.auth.presentation.request.LoginRequest;
import com.example.kakao_clone.global.auth.presentation.request.SignupRequest;
import com.example.kakao_clone.global.auth.presentation.response.TokenResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final BCryptPasswordEncoder encoder;
    private final long COOKIE_EXPIRATION = 86400;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest signupRequest) {
        String encodedPassword = encoder.encode(signupRequest.getPassword());
        SignupRequest newSignupRequest = SignupRequest.encodePassword(signupRequest, encodedPassword);
        userService.register(newSignupRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);

        HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenResponse.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(true)
                .build();
        System.out.println("tokenDto.getAccessToken() = " + tokenResponse.getAccessToken());
        System.out.println("tokenDto.getRefreshToken = " + tokenResponse.getRefreshToken());
        System.out.println("tokenDto.getGrantType() = " + tokenResponse.getGrantType());
        System.out.println("tokenDto.getExpiresIn() = " + tokenResponse.getExpiresIn());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .build();
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {
        TokenResponse reissuedTokenResponse = authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenResponse != null) {
            System.out.println("재발급 성공");
            // RT 저장
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenResponse.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .build();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenResponse.getAccessToken())
                    .build();
        } else {
            System.out.println("재발급 실패");
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String requestAccessToken) {
        authService.logout(requestAccessToken);
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }
}
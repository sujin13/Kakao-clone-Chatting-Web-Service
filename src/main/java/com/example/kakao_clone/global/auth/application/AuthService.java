package com.example.kakao_clone.global.auth.application;

import com.example.kakao_clone.feature.user.domain.User;
import com.example.kakao_clone.feature.user.domain.repository.UserRepository;
import com.example.kakao_clone.global.auth.jwt.JwtTokenProvider;
import com.example.kakao_clone.global.auth.presentation.request.LoginRequest;
import com.example.kakao_clone.global.auth.presentation.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final String SERVER = "Server";

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = userRepository.findByUserId(loginRequest.getUserId()).orElseThrow(()->{
            throw new IllegalStateException("ID를 다시 확인하세요.");
        });

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return generateToken(SERVER, authentication.getName(), getAuthorities(authentication));
    }

    public boolean validate(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        return jwtTokenProvider.validateAccessTokenOnlyExpired(requestAccessToken);
    }

    @Transactional
    public TokenResponse reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
        String principal = getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT(" + SERVER + "):" + principal);
        if (refreshTokenInRedis == null) {
            return null;
        }

        if(!jwtTokenProvider.validateRefreshToken(requestRefreshToken) || !refreshTokenInRedis.equals(requestRefreshToken)) {
            redisService.deleteValues("RT(" + SERVER + "):" + principal);
            return null;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        redisService.deleteValues("RT(" + SERVER + "):" + principal);
        TokenResponse tokenResponse = jwtTokenProvider.createToken(principal, authorities);
        saveRefreshToken(SERVER, principal, tokenResponse.getRefreshToken());
        return tokenResponse;
    }

    @Transactional
    public TokenResponse generateToken(String provider, String email, String authorities) {
        if(redisService.getValues("RT(" + provider + "):" + email) != null) {
            redisService.deleteValues("RT(" + provider + "):" + email);
        }

        TokenResponse tokenResponse = jwtTokenProvider.createToken(email, authorities);
        saveRefreshToken(provider, email, tokenResponse.getRefreshToken());
        return tokenResponse;
    }

    @Transactional
    public void saveRefreshToken(String provider, String principal, String refreshToken) {
        redisService.setValuesWithTimeout("RT(" + provider + "):" + principal, refreshToken, jwtTokenProvider.getTokenExpirationTime(refreshToken));
    }

    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public String getPrincipal(String requestAccessToken) {
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }

    public String resolveToken(String requestAccessTokenInHeader) {
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith("Bearer ")) {
            return requestAccessTokenInHeader.substring(7);
        }
        return null;
    }

    @Transactional
    public void logout(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        String principal = getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT(" + SERVER + "):" + principal);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues("RT(" + SERVER + "):" + principal);
        }

        long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
        redisService.setValuesWithTimeout(requestAccessToken,
                "logout",
                expiration);
    }
}
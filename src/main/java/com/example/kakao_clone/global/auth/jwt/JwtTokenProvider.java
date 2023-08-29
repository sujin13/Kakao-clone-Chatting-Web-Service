package com.example.kakao_clone.global.auth.jwt;

import com.example.kakao_clone.feature.user.application.UserService;
import com.example.kakao_clone.global.auth.application.UserDetailsImpl;
import com.example.kakao_clone.global.auth.application.UserDetailsServiceImpl;
import com.example.kakao_clone.global.auth.application.RedisService;
import com.example.kakao_clone.global.auth.presentation.response.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Transactional(readOnly = true)
public class JwtTokenProvider implements InitializingBean {
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final RedisService redisService;

    private static final String AUTHORITIES_KEY = "role";
    private static final String ID_KEY = "userid";

    private static final String url = "https://localhost:8080";

    private final String secretKey;
    private static Key signingKey;

    private final Long accessTokenValidityInMilliseconds;
    private final Long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            UserDetailsServiceImpl userDetailsService,
            UserService userService, RedisService redisService,
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") Long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") Long refreshTokenValidityInMilliseconds) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.redisService = redisService;
        this.secretKey = secretKey;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        signingKey = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    @Transactional
    public TokenResponse createToken(String userid, String authorities){
        Long now = System.currentTimeMillis();


        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
                .setSubject("access-token")
                .claim(url, true)
                .claim(ID_KEY, userid)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();


        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
                .setSubject("refresh-token")
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();

        return new TokenResponse(accessToken, refreshToken, "Bearer", accessTokenValidityInMilliseconds);
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Authentication getAuthentication(String token) {
        String id = getClaims(token).get(ID_KEY).toString();
        UserDetailsImpl userDetailsImpl = userDetailsService.loadUserByUsername(id);
        return new UsernamePasswordAuthenticationToken(userDetailsImpl, "", userDetailsImpl.getAuthorities());
    }

    public long getTokenExpirationTime(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    public boolean validateRefreshToken(String refreshToken){
        try {
            if (redisService.getValues(refreshToken).equals("delete")) {
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        } catch (NullPointerException e){
            log.error("JWT Token is empty.");
        }
        return false;
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            if (redisService.getValues(accessToken) != null
                    && redisService.getValues(accessToken).equals("logout")) {
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch(ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAccessTokenOnlyExpired(String accessToken) {
        try {
            return getClaims(accessToken)
                    .getExpiration()
                    .before(new Date());
        } catch(ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
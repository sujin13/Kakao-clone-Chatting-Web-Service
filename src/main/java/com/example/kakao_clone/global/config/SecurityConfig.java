package com.example.kakao_clone.global.config;

import com.example.kakao_clone.global.auth.jwt.JwtAccessDeniedHandler;
import com.example.kakao_clone.global.auth.jwt.JwtAuthenticationEntryPoint;
import com.example.kakao_clone.global.auth.jwt.JwtAuthenticationFilter;
import com.example.kakao_clone.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.example.kakao_clone.global.config", "com.example.kakao_clone.auth"})
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(options -> options.sameOrigin()))
                .exceptionHandling(request -> request
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers("/").permitAll()
                        .antMatchers("/swagger-ui/**", "/api-docs/**", "/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}

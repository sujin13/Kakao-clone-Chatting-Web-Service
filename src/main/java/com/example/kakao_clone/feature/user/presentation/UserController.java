package com.example.kakao_clone.feature.user.presentation;

import com.example.kakao_clone.feature.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
}
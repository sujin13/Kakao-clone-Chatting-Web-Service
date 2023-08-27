package com.example.kakao_clone.feature.friend.presentation;

import com.example.kakao_clone.feature.friend.application.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
}

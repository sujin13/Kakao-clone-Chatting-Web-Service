package com.example.kakao_clone.friend.presentation;

import com.example.kakao_clone.friend.application.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
}

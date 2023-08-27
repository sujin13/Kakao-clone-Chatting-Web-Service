package com.example.kakao_clone.chat.presentation;

import com.example.kakao_clone.chat.application.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
}

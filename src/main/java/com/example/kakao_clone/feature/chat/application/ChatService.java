package com.example.kakao_clone.feature.chat.application;

import com.example.kakao_clone.feature.chat.domain.repository.ChatRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
}

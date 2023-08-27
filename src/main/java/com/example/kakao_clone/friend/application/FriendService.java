package com.example.kakao_clone.friend.application;

import com.example.kakao_clone.friend.domain.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
}

package com.example.kakao_clone.chat.domain.repository;

import com.example.kakao_clone.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}

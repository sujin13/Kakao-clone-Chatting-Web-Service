package com.example.kakao_clone.feature.friend.domain.repository;

import com.example.kakao_clone.feature.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
}

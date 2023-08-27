package com.example.kakao_clone.feature.user.domain.repository;

import com.example.kakao_clone.feature.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
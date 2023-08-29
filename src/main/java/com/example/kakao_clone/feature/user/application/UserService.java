package com.example.kakao_clone.feature.user.application;

import com.example.kakao_clone.feature.user.domain.User;
import com.example.kakao_clone.feature.user.domain.repository.UserRepository;
import com.example.kakao_clone.global.auth.domain.Role;
import com.example.kakao_clone.global.auth.presentation.request.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private final UserRepository userRepository;


    public void register(SignupRequest signupRequest) {
        validateDuplicateUser(signupRequest.getUserId());

        User user = User.builder()
                .role(Role.USER)
                .userId(signupRequest.getUserId())
                .password(signupRequest.getPassword())
                .name(signupRequest.getName())
                .statusMessage(signupRequest.getStatusMessage())
                .profileImageUrl("")
                .backgroundImageUrl("")
                .lastLoginDate(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    @Transactional
    private void validateDuplicateUser(String userid){
        Optional<User> userModel = userRepository.findByUserId(userid);
        if (userModel.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
}
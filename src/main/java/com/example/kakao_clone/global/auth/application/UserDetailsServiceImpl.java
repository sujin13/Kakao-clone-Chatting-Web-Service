package com.example.kakao_clone.global.auth.application;

import com.example.kakao_clone.feature.user.domain.User;
import com.example.kakao_clone.feature.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String userId) throws UsernameNotFoundException {
        User findUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디로 찾을 수 없습니다. -> " + userId));

        return new UserDetailsImpl(findUser);
    }
}

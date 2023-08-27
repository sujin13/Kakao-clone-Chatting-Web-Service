package com.example.kakao_clone.feature.user.domain;

import com.example.kakao_clone.feature.chat.domain.Chat;
import com.example.kakao_clone.global.common.BaseEntity;
import com.example.kakao_clone.feature.friend.domain.Friend;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String password;

    private String name;

    private String statusMessage;

    private String profileImageUrl;

    private LocalDateTime lastLoginDate;

    @OneToMany(mappedBy = "user")
    private List<Chat> chatList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Friend> friendList = new ArrayList<>();
}

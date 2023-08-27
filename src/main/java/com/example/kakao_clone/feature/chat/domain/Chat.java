package com.example.kakao_clone.feature.chat.domain;

import com.example.kakao_clone.global.common.BaseEntity;
import com.example.kakao_clone.feature.room.domain.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    private String sendKakaoId;

    private String message;

    private Integer notRead;
}

package com.server.cogito.user.domain;

import com.server.cogito.common.entity.BaseTimeEntity;
import com.server.cogito.common.entity.Status;
import lombok.*;

import javax.persistence.*;


@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String nickname;

    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Enumerated(EnumType.STRING)
    private Provider provider; //kakao,google,facebook,normal

    @Builder
    public User(Long id, String email, String nickname, Provider provider) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.status = Status.ACTIVE;
        this.authority = Authority.ROLE_USER;
        this.provider = provider;
    }


}

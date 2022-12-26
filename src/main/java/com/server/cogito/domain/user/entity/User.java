package com.server.cogito.domain.user.entity;

import com.server.cogito.domain.auth.dto.response.KaKaoUser;
import com.server.cogito.domain.post.entity.Post;
import com.server.cogito.domain.user.enums.Authority;
import com.server.cogito.domain.user.enums.Provider;
import com.server.cogito.global.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String nickname;

    private String profileImgUrl;

    private String introduce;

    private int score;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Builder
    public User(String email, String nickname, Provider provider) {
        this.email = email;
        this.nickname = nickname;
        this.authority = Authority.ROLE_USER;
        this.provider = provider;
        this.score++;
    }

    public void addScore(int num){
        this.score+=num;
    }

}

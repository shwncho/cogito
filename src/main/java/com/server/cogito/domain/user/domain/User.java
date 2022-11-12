package com.server.cogito.domain.user.domain;

import com.server.cogito.domain.post.domain.Post;
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
    public User(Long id, String email, String nickname, Provider provider) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.authority = Authority.ROLE_USER;
        this.provider = provider;
    }


}

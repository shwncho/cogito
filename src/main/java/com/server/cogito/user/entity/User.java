package com.server.cogito.user.entity;

import com.server.cogito.post.entity.Post;
import com.server.cogito.user.dto.request.UserRequest;
import com.server.cogito.user.enums.Authority;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.common.entity.BaseEntity;
import lombok.*;
import org.springframework.util.StringUtils;

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

    @Column(columnDefinition = "TEXT")
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
    public User(Long id, String email, String nickname, String profileImgUrl, String introduce, Provider provider) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.introduce = introduce;
        this.score++;
        this.authority = Authority.ROLE_USER;
        this.provider = provider;
    }

    public void addScore(int num){
        this.score+=num;
    }

    public void subtractScore(int num){
        this.score-=num;
    }

    public void change(UserRequest userRequest){
        changeNickname(userRequest.getNickname());
        changeProfileImgUrl(userRequest.getProfileImgUrl());
        changeIntroduce(userRequest.getIntroduce());
    }

    private void changeNickname(String nickname){
        if(StringUtils.hasText(nickname)){
            this.nickname=nickname;
        }
    }

    private void changeProfileImgUrl(String profileImgUrl){
        if(profileImgUrl!=null){
            this.profileImgUrl=profileImgUrl;
        }

    }

    private void changeIntroduce(String introduce){
        this.introduce=introduce;
    }

}

package com.server.cogito.domain.post.entity;

import com.server.cogito.domain.comment.entity.Comment;
import com.server.cogito.domain.file.entity.PostFile;
import com.server.cogito.domain.like.entity.Likes;
import com.server.cogito.domain.tag.entity.Tag;
import com.server.cogito.domain.user.entity.User;
import com.server.cogito.global.common.entity.BaseEntity;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    private boolean selected;

    private int viewCnt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<PostFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public static Post of(String title, String content, User user){
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }
}

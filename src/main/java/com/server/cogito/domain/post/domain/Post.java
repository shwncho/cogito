package com.server.cogito.domain.post.domain;

import com.server.cogito.domain.comment.Comment;
import com.server.cogito.domain.file.domain.PostFile;
import com.server.cogito.domain.like.domain.Likes;
import com.server.cogito.domain.tag.domain.Tag;
import com.server.cogito.domain.user.domain.User;
import com.server.cogito.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "post")
    private List<PostFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

}

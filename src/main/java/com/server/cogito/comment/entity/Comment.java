package com.server.cogito.comment.entity;

import com.server.cogito.post.entity.Post;
import com.server.cogito.user.entity.User;
import com.server.cogito.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int selected;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> child = new ArrayList<>();

    private int likeCnt;

    @Builder
    public Comment(String content, User user, Post post, Comment parent) {
        this.post = post;
        this.parent = parent;
        this.content = content;
        this.user = user;

    }

    public void changeComment(String content){
        this.content = content;
    }

    public void deleteComment(){
        this.setStatus(Status.INACTIVE);
    }

    public void addLike(){
        this.likeCnt++;
    }

    public void subtractLike(){
        this.likeCnt--;
    }

    public void selectComment(){
        this.selected = 1;
    }
}

package com.server.cogito.notification.entity;

import com.server.cogito.comment.entity.Comment;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.post.entity.Post;
import com.server.cogito.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="comment_id", nullable = false)
    private Comment comment;

    private String content;

    private String url;

    private boolean isRead;

    @Builder
    public Notification(User receiver, Post post, Comment comment, String content, String url, boolean isRead) {
        this.receiver = receiver;
        this.post = post;
        this.comment = comment;
        this.content= content;
        this.url = url;
        this.isRead = isRead;
    }

    public void read() {
        this.isRead = true;
    }
}

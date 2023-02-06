package com.server.cogito.report.entity;

import com.server.cogito.comment.entity.Comment;
import com.server.cogito.post.entity.Post;
import com.server.cogito.user.entity.User;
import com.server.cogito.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    private String reason;

    private int reportCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public Report(String reason, int reportCnt, User user, Post post, Comment comment) {
        this.reason = reason;
        this.reportCnt = reportCnt;
        this.user = user;
        this.post = post;
        this.comment = comment;
    }

    public void addReportCnt(){
        this.reportCnt++;
    }
}

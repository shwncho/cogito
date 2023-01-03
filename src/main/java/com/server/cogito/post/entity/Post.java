package com.server.cogito.post.entity;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.file.entity.PostFile;
import com.server.cogito.tag.entity.Tag;
import com.server.cogito.user.entity.User;
import lombok.*;
import org.hibernate.annotations.BatchSize;

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

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<Tag> tags = new ArrayList<>();

    private int linkCnt;


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

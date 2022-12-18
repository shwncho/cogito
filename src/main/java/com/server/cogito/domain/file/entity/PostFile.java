package com.server.cogito.domain.file.entity;

import com.server.cogito.domain.post.entity.Post;
import com.server.cogito.global.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_file_id")
    private Long id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name="post_id")
    private Post post;

    public PostFile(String url) {
        this.url = url;
    }

    public void changePost(Post post){
        this.post=post;
        this.post.getFiles().add(this);
    }
}

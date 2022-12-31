package com.server.cogito.tag.entity;

import com.server.cogito.post.entity.Post;
import com.server.cogito.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_id")
    private Post post;

    public Tag(String content) {
        this.content = content;
    }

    public void changePost(Post post){
        this.post=post;
        this.post.getTags().add(this);
    }
}

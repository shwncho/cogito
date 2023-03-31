package com.server.cogito.post.entity;

import com.server.cogito.comment.entity.Comment;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.file.entity.PostFile;
import com.server.cogito.post.dto.request.UpdatePostRequest;
import com.server.cogito.tag.entity.Tag;
import com.server.cogito.user.entity.User;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean selected;

    private int viewCnt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post",cascade = CascadeType.REMOVE)
    private List<PostFile> files = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post",cascade = CascadeType.REMOVE)
    private List<Tag> tags = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    private int likeCnt;


    @Builder
    public Post(Long id,String title, String content, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void change(UpdatePostRequest updatePostRequest){
        changeTitle(updatePostRequest.getTitle());
        changeContent(updatePostRequest.getContent());
    }

    private void changeTitle(String title){
        if(StringUtils.hasText(title)){
            this.title=title;
        }
    }

    private void changeContent(String content){
        if(content!=null){
            this.content = content;
        }
    }

    public void deletePost(){
        this.setStatus(Status.INACTIVE);
    }

    public void addLike(){
        this.likeCnt++;
    }

    public void subtractLike(){
        this.likeCnt--;
    }
}

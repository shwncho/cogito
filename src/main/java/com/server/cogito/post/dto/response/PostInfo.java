package com.server.cogito.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.cogito.file.entity.PostFile;
import com.server.cogito.post.entity.Post;
import com.server.cogito.tag.entity.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostInfo {

    private Long postId;

    private String title;

    private String content;

    private List<String> tags;

    private String nickname;

    private String profileImgUrl;

    private int score;

    private int commentCnt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    public static PostInfo from (Post post){
        return PostInfo.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags().stream().map(Tag::getContent).collect(Collectors.toList()))
                .nickname(post.getUser().getNickname())
                .profileImgUrl(post.getUser().getProfileImgUrl())
                .score(post.getUser().getScore())
                .commentCnt(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .build();
    }
}

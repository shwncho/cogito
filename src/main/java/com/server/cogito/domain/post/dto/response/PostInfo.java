package com.server.cogito.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.cogito.domain.post.entity.Post;
import com.server.cogito.domain.tag.entity.Tag;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostInfo {

    private String title;

    private String content;

    private List<String> tags;

    private String nickname;

    private int score;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    public static PostInfo from (Post post){
        return PostInfo.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags().stream().map(Tag::getContent).collect(Collectors.toList()))
                .nickname(post.getUser().getNickname())
                .score(post.getUser().getScore())
                .createdAt(post.getCreatedAt())
                .build();
    }
}

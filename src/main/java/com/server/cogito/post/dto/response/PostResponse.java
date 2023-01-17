package com.server.cogito.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.cogito.comment.dto.response.CommentResponse;
import com.server.cogito.file.entity.PostFile;
import com.server.cogito.post.entity.Post;
import com.server.cogito.tag.entity.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long postId;

    private String title;

    private String content;

    private List<String> tags;

    private List<String> files;

    private String nickname;

    private String profileImgUrl;

    @JsonProperty("isMe")
    private boolean isMe;

    private int score;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    private List<CommentResponse> commentResponses;

    public static PostResponse from(Long userId, Post post, List<CommentResponse> commentResponses){
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags().stream().map(Tag::getContent).collect(Collectors.toList()))
                .files(post.getFiles().stream().map(PostFile::getUrl).collect(Collectors.toList()))
                .nickname(post.getUser().getNickname())
                .profileImgUrl(post.getUser().getProfileImgUrl())
                .isMe(Objects.equals(userId, post.getUser().getId()))
                .score(post.getUser().getScore())
                .commentResponses(commentResponses)
                .build();
    }
}

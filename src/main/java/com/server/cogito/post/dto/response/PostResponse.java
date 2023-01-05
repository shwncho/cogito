package com.server.cogito.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.cogito.comment.dto.response.CommentResponse;
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
public class PostResponse {

    private String title;

    private String content;

    private List<String> tags;

    private List<String> files;

    private String nickname;

    private String profileImgUrl;

    private int score;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private List<CommentResponse> commentResponses;

    public static PostResponse from(Post post, List<CommentResponse> commentResponses){
        return PostResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags().stream().map(Tag::getContent).collect(Collectors.toList()))
                .files(post.getFiles().stream().map(PostFile::getUrl).collect(Collectors.toList()))
                .nickname(post.getUser().getNickname())
                .profileImgUrl(post.getUser().getProfileImgUrl())
                .score(post.getUser().getScore())
                .commentResponses(commentResponses)
                .build();
    }
}

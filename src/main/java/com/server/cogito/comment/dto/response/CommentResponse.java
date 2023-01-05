package com.server.cogito.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.cogito.comment.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long commentId;
    private String content;
    private int selected;
    private int likeCnt;
    private Long userId;
    private String nickname;
    private int score;
    private String profileImgUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    @Builder.Default
    private List<CommentResponse> children = new ArrayList<>();

    @Builder
    public CommentResponse(Long commentId, String content, int selected, int likeCnt,
                           Long userId, String nickname, int score, String profileImgUrl, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.selected = selected;
        this.likeCnt = likeCnt;
        this.userId = userId;
        this.nickname = nickname;
        this.score = score;
        this.profileImgUrl = profileImgUrl;
        this.createdAt = createdAt;
    }

    public static CommentResponse from(Comment comment){
        return CommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .selected(comment.getSelected())
                .likeCnt(comment.getLikeCnt())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .score(comment.getUser().getScore())
                .profileImgUrl(comment.getUser().getProfileImgUrl())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

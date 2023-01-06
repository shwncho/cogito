package com.server.cogito.comment.controller;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.dto.request.UpdateCommentRequest;
import com.server.cogito.comment.service.CommentService;
import com.server.cogito.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("")
    public void createComment(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid CommentRequest commentRequest){
        commentService.createComment(authUser,commentRequest);
    }

    @PatchMapping("{commentId}")
    public void updateComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId,
                              @RequestBody @Valid UpdateCommentRequest updateCommentRequest){
        commentService.updateComment(authUser, commentId, updateCommentRequest);
    }

    @PatchMapping("{commentId}/status")
    public void deleteComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId){
        commentService.deleteComment(authUser,commentId);
    }
}

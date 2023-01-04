package com.server.cogito.comment.controller;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.service.CommentService;
import com.server.cogito.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("")
    public void createComment(@AuthenticationPrincipal AuthUser authUser, @RequestBody CommentRequest commentRequest){
        commentService.createComment(authUser,commentRequest);
    }
}

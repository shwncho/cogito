package com.server.cogito.domain.post.controller;

import com.server.cogito.domain.post.dto.request.CreatePostRequest;
import com.server.cogito.domain.post.service.PostService;
import com.server.cogito.global.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public void createPost(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid CreatePostRequest request){
        postService.createPost(authUser,request);
    }
}

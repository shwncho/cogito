package com.server.cogito.domain.post.controller;

import com.server.cogito.domain.post.dto.request.CreatePostRequest;
import com.server.cogito.domain.post.dto.response.CreatePostResponse;
import com.server.cogito.domain.post.dto.response.PostPageResponse;
import com.server.cogito.domain.post.service.PostService;
import com.server.cogito.global.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public CreatePostResponse createPost(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid CreatePostRequest request){
        return CreatePostResponse.from(postService.createPost(authUser,request));
    }

    @GetMapping("")
    public PostPageResponse getPosts(@PageableDefault(page = 1) Pageable pageable){
        return postService.getPosts(pageable);
    }
}

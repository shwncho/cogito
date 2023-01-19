package com.server.cogito.post.controller;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.post.dto.request.PostRequest;
import com.server.cogito.post.dto.request.UpdatePostRequest;
import com.server.cogito.post.dto.response.CreatePostResponse;
import com.server.cogito.post.dto.response.PostPageResponse;
import com.server.cogito.post.dto.response.PostResponse;
import com.server.cogito.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public CreatePostResponse createPost(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid PostRequest request){
        return postService.createPost(authUser,request);
    }

    @GetMapping("")
    public PostPageResponse getPosts(@RequestParam(required = false) String query, Pageable pageable){
        return postService.getPosts(query,pageable);
    }

    @GetMapping("/{postId}")
    public PostResponse getPost(@PathVariable Long postId){
        return postService.getPost(postId);
    }

    @PatchMapping("/{postId}")
    public void updatePost(@PathVariable Long postId, @RequestBody @Valid UpdatePostRequest updatePostRequest){
        postService.updatePost(postId,updatePostRequest);
    }

    @PatchMapping("/{postId}/status")
    public void deletePost(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId){
        postService.deletePost(authUser, postId);
    }

    @PatchMapping("/{postId}/like")
    public void likePost(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId){
        postService.likePost(authUser, postId);
    }

    @PatchMapping("/{postId}/dislike")
    public void dislikePost(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId){
        postService.dislikePost(authUser, postId);
    }
}

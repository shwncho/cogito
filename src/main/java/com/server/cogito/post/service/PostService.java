package com.server.cogito.post.service;

import com.server.cogito.comment.dto.response.CommentResponse;
import com.server.cogito.comment.entity.Comment;
import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.file.entity.PostFile;
import com.server.cogito.post.dto.request.PostRequest;
import com.server.cogito.post.dto.response.CreatePostResponse;
import com.server.cogito.post.dto.response.PostInfo;
import com.server.cogito.post.dto.response.PostPageResponse;
import com.server.cogito.post.dto.response.PostResponse;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.repository.PostRepository;
import com.server.cogito.tag.entity.Tag;
import com.server.cogito.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CreatePostResponse createPost(AuthUser authUser, PostRequest request){
        User user = authUser.getUser();
        Post post = Post.of(request.getTitle(), request.getContent(), user);
        savePostFilesAndTags(request, post);
        user.addScore(2);
        return CreatePostResponse.from(postRepository.save(post).getId());
    }

    private static void savePostFilesAndTags(PostRequest request, Post post) {
        request.getFiles().forEach(s -> {
            PostFile postFile = new PostFile(s);
            postFile.changePost(post);
        });
        request.getTags().forEach(s -> {
            Tag tag = new Tag(s);
            tag.changePost(post);
        });
    }

    @Transactional(readOnly = true)
    public PostPageResponse getPosts(Pageable pageable){
        PageRequest request = PageRequest.of(pageable.getPageNumber()-1, 10, Sort.Direction.DESC,"createdAt");
        Page<Post> posts = postRepository.findAll(request);
        return getPostPageResponse(posts);
    }

    private static PostPageResponse getPostPageResponse(Page<Post> posts) {
        return PostPageResponse.from(posts.getContent()
                .stream()
                .map(PostInfo::from)
                .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId){
        Post post = postRepository.findPostByIdAndStatus(postId, BaseEntity.Status.ACTIVE)
                .orElseThrow(PostNotFoundException::new);
        return PostResponse.from(post, convert(commentRepository.findCommentsByPostId(post.getId())));
    }

    private List<CommentResponse> convert(List<Comment> comments){
        List<CommentResponse> result = new ArrayList<>();
        Map<Long, CommentResponse> map = new HashMap<>();
        comments.forEach(c ->{
            CommentResponse response = CommentResponse.from(c);
            map.put(response.getCommentId(), response);
            if(c.getParent()!=null) map.get(c.getParent().getId()).getChildren().add(response);
            else result.add(response);
        });
        return result;
    }
}

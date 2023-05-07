package com.server.cogito.post.service;

import com.server.cogito.comment.dto.response.CommentResponse;
import com.server.cogito.comment.entity.Comment;
import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.common.exception.user.UserNotFoundException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.file.entity.PostFile;
import com.server.cogito.file.repository.PostFileRepository;
import com.server.cogito.post.dto.request.PostRequest;
import com.server.cogito.post.dto.request.UpdatePostRequest;
import com.server.cogito.post.dto.response.CreatePostResponse;
import com.server.cogito.post.dto.response.PostInfo;
import com.server.cogito.post.dto.response.PostPageResponse;
import com.server.cogito.post.dto.response.PostResponse;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.repository.PostRepository;
import com.server.cogito.tag.entity.Tag;
import com.server.cogito.tag.repository.TagRepository;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostFileRepository postFileRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreatePostResponse createPost(AuthUser authUser, PostRequest request){
        User user = userRepository.findByEmailAndStatus(authUser.getUsername(), BaseEntity.Status.ACTIVE)
                .orElseThrow(UserNotFoundException::new);
        Post post = create(request.getTitle(),request.getContent(),user);
        savePostFilesAndTags(request, post);
        user.addScore(2);
        return CreatePostResponse.from(postRepository.save(post).getId());
    }

    private Post create(String title, String content, User user){
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }

    private void savePostFilesAndTags(PostRequest request, Post post) {
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
    public PostPageResponse getPosts(String query, Pageable pageable){
        if(StringUtils.hasText(query))
            return getPostPageResponse(postRepository.findWithSearchConditions(query, pageable));
        return getPostPageResponse(postRepository.findWithoutSearchConditions(pageable));
    }

    private PostPageResponse getPostPageResponse(Page<Post> posts) {
        return PostPageResponse.of(posts.getContent()
                .stream()
                .map(PostInfo::from)
                .collect(Collectors.toList()), posts.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(AuthUser authUser, Long postId){
        Long userId = authUser==null ? 0L : authUser.getUserId();
        Post post = postRepository.findPostByIdAndStatus(postId, BaseEntity.Status.ACTIVE)
                .orElseThrow(PostNotFoundException::new);
        return PostResponse.from(userId, post, convert(userId, commentRepository.findCommentsByPostId(post.getId())));
    }

    private List<CommentResponse> convert(Long userId, List<Comment> comments){
        List<CommentResponse> result = new ArrayList<>();
        Map<Long, CommentResponse> map = new HashMap<>();
        comments.forEach(c ->{
            CommentResponse response = CommentResponse.from(userId, c);
            map.put(response.getCommentId(), response);
            if(c.getParent()!=null) map.get(c.getParent().getId()).getChildren().add(response);
            else result.add(response);
        });
        return result;
    }

    @Transactional
    public void updatePost(Long postId, UpdatePostRequest updatePostRequest){
        Post post = postRepository.findPostByIdAndStatus(postId, BaseEntity.Status.ACTIVE)
                .orElseThrow(PostNotFoundException::new);
        postFileRepository.deleteAllByPost(post);
        tagRepository.deleteAllByPost(post);
        savePostFilesAndTags(updatePostRequest,post);
        post.change(updatePostRequest);
    }

    private void savePostFilesAndTags(UpdatePostRequest request, Post post) {
        request.getFiles().forEach(s -> {
            PostFile postFile = new PostFile(s);
            postFile.changePost(post);
        });
        request.getTags().forEach(s -> {
            Tag tag = new Tag(s);
            tag.changePost(post);
        });
    }

    @Transactional
    public void deletePost(AuthUser authUser, Long postId){
        Post post = postRepository.findByIdAndStatus(postId, BaseEntity.Status.ACTIVE)
                .orElseThrow(PostNotFoundException::new);
        validateUserId(authUser,post);
        post.getUser().subtractScore(2);
        post.deletePost();
    }

    private void validateUserId(AuthUser authUser, Post post) {
        if(!Objects.equals(authUser.getUserId(), post.getUser().getId())){
            throw new UserInvalidException(UserErrorCode.USER_INVALID);
        }
    }

    @Transactional
    public void likePost(AuthUser authUser, Long postId){
        Post post = postRepository.findByIdAndStatus(postId, BaseEntity.Status.ACTIVE)
                .orElseThrow(PostNotFoundException::new);
        validateEqualUserId(authUser,post);
        postRepository.increaseLikeCount(postId);
    }

    @Transactional
    public void dislikePost(AuthUser authUser, Long postId){
        Post post = postRepository.findByIdAndStatus(postId, BaseEntity.Status.ACTIVE)
                .orElseThrow(PostNotFoundException::new);
        validateEqualUserId(authUser,post);
        postRepository.decreaseLikeCount(postId);
    }


    private void validateEqualUserId(AuthUser authUser, Post post) {
        if(Objects.equals(authUser.getUserId(), post.getUser().getId())){
            throw new UserInvalidException(UserErrorCode.USER_INVALID);
        }
    }

}

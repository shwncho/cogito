package com.server.cogito.domain.post.service;

import com.server.cogito.domain.file.entity.PostFile;
import com.server.cogito.domain.post.dto.request.CreatePostRequest;
import com.server.cogito.domain.post.entity.Post;
import com.server.cogito.domain.post.repository.PostRepository;
import com.server.cogito.domain.tag.entity.Tag;
import com.server.cogito.domain.user.entity.User;
import com.server.cogito.domain.user.repository.UserRepository;
import com.server.cogito.global.common.entity.BaseEntity;
import com.server.cogito.global.common.exception.ApplicationException;
import com.server.cogito.global.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.server.cogito.global.common.exception.user.UserErrorCode.USER_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(AuthUser authUser, CreatePostRequest request){
        User user = userRepository.findByEmailAndStatus(authUser.getUsername(), BaseEntity.Status.ACTIVE)
                .orElseThrow(()-> new ApplicationException(USER_NOT_EXIST));

        Post post = Post.of(request.getTitle(), request.getContent(), user);
        savePostFilesAndTags(request, post);
        user.addScore(2);
        return postRepository.save(post).getId();
    }

    private static void savePostFilesAndTags(CreatePostRequest request, Post post) {
        request.getFiles().forEach(s -> {
            PostFile postFile = new PostFile(s);
            postFile.changePost(post);
        });
        request.getTags().forEach(s -> {
            Tag tag = new Tag(s);
            tag.changePost(post);
        });
    }
}

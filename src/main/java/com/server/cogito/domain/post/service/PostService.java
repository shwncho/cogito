package com.server.cogito.domain.post.service;

import com.server.cogito.domain.file.entity.PostFile;
import com.server.cogito.domain.post.dto.request.CreatePostRequest;
import com.server.cogito.domain.post.dto.response.PostInfo;
import com.server.cogito.domain.post.dto.response.PostPageResponse;
import com.server.cogito.domain.post.entity.Post;
import com.server.cogito.domain.post.repository.PostRepository;
import com.server.cogito.domain.tag.entity.Tag;
import com.server.cogito.domain.user.entity.User;
import com.server.cogito.domain.user.repository.UserRepository;
import com.server.cogito.global.common.entity.BaseEntity;
import com.server.cogito.global.common.exception.ApplicationException;
import com.server.cogito.global.common.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
}

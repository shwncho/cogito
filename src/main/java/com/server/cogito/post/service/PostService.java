package com.server.cogito.post.service;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.user.UserNotFoundException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.file.entity.PostFile;
import com.server.cogito.post.dto.request.CreatePostRequest;
import com.server.cogito.post.dto.response.PostInfo;
import com.server.cogito.post.dto.response.PostPageResponse;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.repository.PostRepository;
import com.server.cogito.tag.entity.Tag;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(AuthUser authUser, CreatePostRequest request){
        User user = userRepository.findByEmailAndStatus(authUser.getUsername(), BaseEntity.Status.ACTIVE)
                .orElseThrow(UserNotFoundException::new);

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

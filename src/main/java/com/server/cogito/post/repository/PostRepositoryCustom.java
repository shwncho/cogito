package com.server.cogito.post.repository;

import com.server.cogito.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    Page<Post> findWithSearchConditions(String query, Pageable pageable);

    Page<Post> findWithoutSearchConditions(Pageable pageable);
}

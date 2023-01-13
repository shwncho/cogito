package com.server.cogito.post.repository;

import com.server.cogito.post.entity.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> findWithSearchConditions(String query, Pageable pageable);

    List<Post> findWithoutSearchConditions(Pageable pageable);
}

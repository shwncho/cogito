package com.server.cogito.post.repository;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findAll(Pageable pageable);

    Optional<Post> findByIdAndStatus(Long postId, BaseEntity.Status status);
}

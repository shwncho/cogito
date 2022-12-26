package com.server.cogito.domain.post.repository;

import com.server.cogito.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Post> findAll(Pageable pageable);
}

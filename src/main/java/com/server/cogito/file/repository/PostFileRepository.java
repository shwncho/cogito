package com.server.cogito.file.repository;

import com.server.cogito.file.entity.PostFile;
import com.server.cogito.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    @Modifying
    @Query(value = "delete from PostFile f where f.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}

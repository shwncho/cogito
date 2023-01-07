package com.server.cogito.tag.repository;

import com.server.cogito.post.entity.Post;
import com.server.cogito.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {

    @Modifying
    @Query(value = "delete from Tag t where t.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}

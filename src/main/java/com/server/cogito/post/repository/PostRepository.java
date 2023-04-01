package com.server.cogito.post.repository;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long>,PostRepositoryCustom{


    //게시물 존재여부 체크
    @Query("select p from Post p" +
            " join fetch p.user u" +
            " where p.id = :postId and p.status = :status")
    Optional<Post> findByIdAndStatus(@Param("postId") Long postId, @Param("status") BaseEntity.Status status);

    //게시물 상세 조회
    @Query("select distinct p from Post p" +
            " join fetch p.user u" +
            " left join fetch p.files f" +
            " where p.id = :postId and p.status = :status")
    Optional<Post> findPostByIdAndStatus(@Param("postId") Long postId, @Param("status") BaseEntity.Status status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value="update Post p set p.likeCnt = p.likeCnt + 1 where p.id = :postId")
    void increaseLikeCount(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update Post p set p.likeCnt = p.likeCnt - 1 where p.id = :postId")
    void decreaseLikeCount(@Param("postId") Long postId);
}

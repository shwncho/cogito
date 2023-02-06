package com.server.cogito.report.repository;

import com.server.cogito.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    Optional<Report> findByPostId(Long postId);

    Optional<Report> findByCommentId(Long commentId);
}

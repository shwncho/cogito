package com.server.cogito.report.repository;

import com.server.cogito.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Long> {

    Optional<Report> findByUserIdAndPostId(Long userId, Long postId);

    Optional<Report> findByUserIdAndCommentId(Long userId, Long commentId);
}

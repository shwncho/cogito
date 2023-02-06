package com.server.cogito.report.service;

import com.server.cogito.comment.entity.Comment;
import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.comment.CommentNotFoundException;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserNotFoundException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.repository.PostRepository;
import com.server.cogito.report.dto.ReportRequest;
import com.server.cogito.report.dto.ReportResponse;
import com.server.cogito.report.entity.Report;
import com.server.cogito.report.repository.ReportRepository;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.server.cogito.common.entity.BaseEntity.Status.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ReportResponse reportPost(AuthUser authUser, Long postId, ReportRequest reportRequest){
        User user = userRepository.findByIdAndStatus(authUser.getUserId(), ACTIVE)
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findByIdAndStatus(postId,ACTIVE)
                .orElseThrow(PostNotFoundException::new);
        Report report = reportRepository.findByUserIdAndPostId(authUser.getUserId(), postId)
                .orElseGet(()->createReportPost(user, post, reportRequest));

        report.addReportCnt();

        int reportCnt = report.getReportCnt();
        if(reportCnt==5){
            String result = "신고 누적으로인해 게시물이 삭제되었습니다.";
            return new ReportResponse(result);
        }

        return new ReportResponse("신고 누적 횟수: "+reportCnt);

    }

    private Report createReportPost(User user, Post post, ReportRequest reportRequest){
        return reportRepository.save(Report.builder()
                .reason(reportRequest.getReason())
                .reportCnt(0)
                .user(user)
                .post(post)
                .comment(null)
                .build());
    }

    @Transactional
    public ReportResponse reportComment(AuthUser authUser, Long commentId, ReportRequest reportRequest){
        User user = userRepository.findByIdAndStatus(authUser.getUserId(), ACTIVE)
                .orElseThrow(UserNotFoundException::new);
        Comment comment = commentRepository.findByIdAndStatus(commentId,ACTIVE)
                .orElseThrow(CommentNotFoundException::new);
        Report report = reportRepository.findByUserIdAndCommentId(authUser.getUserId(), commentId)
                .orElseGet(()->createReportComment(user, comment, reportRequest));

        report.addReportCnt();

        int reportCnt = report.getReportCnt();
        if(reportCnt==5){
            String result = "신고 누적으로인해 게시물이 삭제되었습니다.";
            return new ReportResponse(result);
        }

        return new ReportResponse("신고 누적 횟수: "+reportCnt);
    }

    private Report createReportComment(User user, Comment comment, ReportRequest reportRequest){
        return reportRepository.save(Report.builder()
                .reason(reportRequest.getReason())
                .reportCnt(0)
                .user(user)
                .post(null)
                .comment(comment)
                .build());

    }
}

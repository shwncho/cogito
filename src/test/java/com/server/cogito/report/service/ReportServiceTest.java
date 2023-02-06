package com.server.cogito.report.service;

import com.server.cogito.comment.repository.CommentRepository;
import com.server.cogito.common.exception.report.DuplicatedReportPostException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.post.entity.Post;
import com.server.cogito.post.repository.PostRepository;
import com.server.cogito.report.dto.ReportRequest;
import com.server.cogito.report.dto.ReportResponse;
import com.server.cogito.report.entity.Report;
import com.server.cogito.report.repository.ReportRepository;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ReportRepository reportRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ReportService reportService;

    @Test
    @DisplayName("게시물 신고 성공 / 최초 신고")
    public void report_post_success() throws Exception {
        //given
        ReportRequest request = new ReportRequest("게시물 신고 테스트");
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = Post.of("신고 게시글 제목","신고 게시글 내용",user);
        Report report = createReportPost(user,post,request);
        given(userRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(user));
        given(postRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(post));
        given(reportRepository.existsByUserIdAndPostId(any(),any()))
                .willReturn(false);
        given(reportRepository.findByPostId(any())
                .orElseGet(()->reportRepository.save(any())))
                .willReturn(report);
        //when
        ReportResponse response = reportService.reportPost(authUser,1L,request);
        //then
        assertThat(report.getReportCnt()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시물 신고 실패 / 중복 신고")
    public void report_post_fail_duplicated() throws Exception {
        //given
        ReportRequest request = new ReportRequest("게시물 신고 테스트");
        User user = mockUser();
        AuthUser authUser = AuthUser.of(user);
        Post post = Post.of("신고 게시글 제목","신고 게시글 내용",user);
        Report report = createReportPost(user,post,request);

        given(userRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(user));
        given(postRepository.findByIdAndStatus(any(),any()))
                .willReturn(Optional.of(post));
        given(reportRepository.existsByUserIdAndPostId(any(),any()))
                .willReturn(true);


        //expected
        assertThatThrownBy(()->reportService.reportPost(authUser,1L,request))
                .isExactlyInstanceOf(DuplicatedReportPostException.class);
    }



    private User mockUser(){
        return User.builder()
                .id(1L)
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }

    private Report createReportPost(User user, Post post, ReportRequest reportRequest){
        return Report.builder()
                .reason(reportRequest.getReason())
                .reportCnt(0)
                .user(user)
                .post(post)
                .build();
    }

}
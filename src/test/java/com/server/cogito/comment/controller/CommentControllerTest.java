package com.server.cogito.comment.controller;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.dto.request.UpdateCommentRequest;
import com.server.cogito.comment.service.CommentService;
import com.server.cogito.common.exception.comment.CommentErrorCode;
import com.server.cogito.common.exception.comment.CommentInvalidException;
import com.server.cogito.common.exception.comment.CommentNotFoundException;
import com.server.cogito.common.exception.post.PostErrorCode;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.support.restdocs.RestDocsSupport;
import com.server.cogito.support.security.WithMockJwt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.server.cogito.support.restdocs.RestDocsConfig.field;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class CommentControllerTest extends RestDocsSupport {

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("?????? ?????? ??????")
    public void create_comment_success() throws Exception {
        //given
        CommentRequest request = CommentRequest.builder()
                .postId(1L)
                .content("?????????")
                .build();
        willDoNothing().given(commentService).createComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/comments")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("????????? id"),
                                fieldWithPath("parentId").type(JsonFieldType.NULL).description("??? ??????????????? null, ?????????????????? ?????? ?????? id"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????")
                        )
                ));


    }

    @Test
    @DisplayName("?????? ?????? ?????? / ???????????? ?????? ?????????")
    public void create_comment_fail_not_found_post() throws Exception {
        //given
        CommentRequest request = CommentRequest.builder()
                .postId(1L)
                .content("?????????")
                .build();
        willThrow(new PostNotFoundException()).given(commentService).createComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/comments")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(PostErrorCode.POST_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(PostErrorCode.POST_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("?????? ?????? ?????? / ???????????? ?????? ?????? ????????? ??????")
    public void create_comment_fail_not_found_parentComment() throws Exception {
        //given
        CommentRequest request = CommentRequest.builder()
                .postId(1L)
                .content("?????????")
                .build();
        willThrow(new CommentNotFoundException()).given(commentService).createComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/comments")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(CommentErrorCode.COMMENT_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(CommentErrorCode.COMMENT_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("?????? ?????? ?????? / ???????????? ?????? ?????? ??????")
    public void create_comment_fail_invalid_parent() throws Exception {
        //given
        CommentRequest request = CommentRequest.builder()
                .postId(1L)
                .parentId(2L)
                .content("?????????")
                .build();
        willThrow(new CommentInvalidException(CommentErrorCode.COMMENT_PARENT_INVALID))
                .given(commentService).createComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/comments")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(CommentErrorCode.COMMENT_PARENT_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(CommentErrorCode.COMMENT_PARENT_INVALID.getMessage())));
    }
    @Test
    @DisplayName("?????? ?????? ??????")
    public void update_comment_success() throws Exception {
        //given
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                        .content("??????")
                        .build();
        willDoNothing().given(commentService).updateComment(any(),any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}", 1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("?????? id")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? / ???????????? ?????? ??????")
    public void update_comment_fail_not_found() throws Exception {
        //given
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("??????")
                .build();
        willThrow(new CommentNotFoundException()).given(commentService).updateComment(any(),any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}", 1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(CommentErrorCode.COMMENT_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(CommentErrorCode.COMMENT_NOT_FOUND.getMessage())));
    }


    @Test
    @DisplayName("?????? ?????? ??????")
    public void delete_comment_success() throws Exception {
        //given
        willDoNothing().given(commentService).deleteComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/status",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("?????? id")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? / ???????????? ?????? ??????")
    public void delete_comment_fail_not_found() throws Exception {
        //given
        willThrow(new CommentNotFoundException()).given(commentService).deleteComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/status",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(CommentErrorCode.COMMENT_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(CommentErrorCode.COMMENT_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("?????? ?????? ?????? / ???????????? ?????? ??????")
    public void delete_comment_fail_invalid_user() throws Exception {
        //given
        willThrow(new UserInvalidException(UserErrorCode.USER_INVALID)).given(commentService).deleteComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/status",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_INVALID.getMessage())));
    }

    @Test
    @DisplayName("?????? ????????? ??????")
    public void like_comment_success() throws Exception {
        //given
        willDoNothing().given(commentService).likeComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/like",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("?????? id")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????? ?????? / ???????????? ?????? ??????")
    public void like_comment_fail_not_found() throws Exception {
        //given
        willThrow(new CommentNotFoundException()).given(commentService).likeComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/like",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(CommentErrorCode.COMMENT_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(CommentErrorCode.COMMENT_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("?????? ????????? ?????? / ???????????? ?????? ?????? ??????")
    public void like_comment_fail_invalid_parent() throws Exception {
        //given
        willThrow(new CommentInvalidException(CommentErrorCode.COMMENT_PARENT_INVALID))
                .given(commentService).likeComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/like",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(CommentErrorCode.COMMENT_PARENT_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(CommentErrorCode.COMMENT_PARENT_INVALID.getMessage())));
    }

    @Test
    @DisplayName("?????? ????????? ?????? / ????????? ?????? ????????? ????????? ??????")
    public void like_comment_fail_invalid_user() throws Exception {
        //given
        willThrow(new UserInvalidException(UserErrorCode.USER_INVALID)).given(commentService).likeComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/like",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_INVALID.getMessage())));
    }

    @Test
    @DisplayName("?????? ????????? ??????")
    public void dislike_comment_success() throws Exception {
        //given
        willDoNothing().given(commentService).dislikeComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/dislike",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("?????? id")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????? ?????? / ???????????? ?????? ??????")
    public void dislike_comment_fail_not_found() throws Exception {
        //given
        willThrow(new CommentNotFoundException()).given(commentService).dislikeComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/dislike",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(CommentErrorCode.COMMENT_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(CommentErrorCode.COMMENT_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("?????? ????????? ?????? / ???????????? ?????? ?????? ??????")
    public void dislike_comment_fail_invalid_parent() throws Exception {
        //given
        willThrow(new CommentInvalidException(CommentErrorCode.COMMENT_PARENT_INVALID))
                .given(commentService).dislikeComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/dislike",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(CommentErrorCode.COMMENT_PARENT_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(CommentErrorCode.COMMENT_PARENT_INVALID.getMessage())));
    }

    @Test
    @DisplayName("?????? ????????? ?????? / ????????? ?????? ????????? ????????? ??????")
    public void dislike_comment_fail_invalid_user() throws Exception {
        //given
        willThrow(new UserInvalidException(UserErrorCode.USER_INVALID)).given(commentService).dislikeComment(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/comments/{commentId}/dislike",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_INVALID.getMessage())));
    }
}
package com.server.cogito.comment.controller;

import com.server.cogito.comment.dto.request.CommentRequest;
import com.server.cogito.comment.service.CommentService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class CommentControllerTest extends RestDocsSupport {

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("댓글 생성 성공")
    public void createComment_success() throws Exception {
        //given
        CommentRequest request = CommentRequest.builder()
                .postId(1L)
                .content("테스트")
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
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 id"),
                                fieldWithPath("parentId").type(JsonFieldType.NULL).description("첫 댓글일경우 null, 대댓글일경우 부모 댓글 id"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                        )
                ));


    }
}
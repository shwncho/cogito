package com.server.cogito.domain.post.controller;

import com.server.cogito.domain.post.dto.request.CreatePostRequest;
import com.server.cogito.domain.post.service.PostService;
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

import java.util.List;

import static com.server.cogito.support.restdocs.RestDocsConfig.field;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class PostControllerTest extends RestDocsSupport {

    @MockBean
    private PostService postService;

    @Test
    @DisplayName("게시물 생성 성공")
    void createPost_success() throws Exception{
        //given
        CreatePostRequest request = createPostRequest();
        given(postService.createPost(any(),any())).willReturn(1L);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/posts")
                        .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));


        //then, docs
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.postId", is(1)))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목").attributes(field("constraints","null, \" \", \"\" 불가")),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 내용").attributes(field("constraints","null,  \" \", \"\"불가")),
                                fieldWithPath("files[]").type(JsonFieldType.ARRAY).optional().description("게시물 이미지 url"),
                                fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("게시물 태그")
                        ),
                        responseFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("생성된 게시물 id")
                        )
                ));



    }

    private static CreatePostRequest createPostRequest() {
        CreatePostRequest request = CreatePostRequest.builder()
                .title("테스트")
                .content("테스트")
                .build();
        request.setFiles(List.of("file1","file2"));
        request.setTags(List.of("tag1","tag2"));
        return request;
    }

    private static CreatePostRequest createPostNullRequest() {
        CreatePostRequest request = CreatePostRequest.builder()
                .title(null)
                .content(null)
                .build();
        request.setFiles(List.of("file1","file2"));
        request.setTags(List.of("tag1","tag2"));
        return request;
    }

    @Test
    @DisplayName("게시물 생성 실패 / 입력 조건에 대한 예외")
    void createPost_fail_not_valid() throws Exception{

        //given
        CreatePostRequest request = createPostNullRequest();


        //when
        ResultActions resultActions = mockMvc.perform(post("/api/posts")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then, docs
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors",hasSize(2)));

    }

}
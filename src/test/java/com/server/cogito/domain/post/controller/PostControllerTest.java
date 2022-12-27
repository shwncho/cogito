package com.server.cogito.domain.post.controller;

import com.server.cogito.domain.post.dto.request.CreatePostRequest;
import com.server.cogito.domain.post.dto.response.PostInfo;
import com.server.cogito.domain.post.dto.response.PostPageResponse;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.server.cogito.support.restdocs.RestDocsConfig.field;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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

    @Test
    @DisplayName("게시물 조회 성공 / 최신순")
    void getPosts_success_latest() throws Exception {

        //given
        PostPageResponse response = PostPageResponse.from(createPostInfo());
        given(postService.getPosts(any())).willReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .param("page","1")
                .contentType(MediaType.APPLICATION_JSON));

        //then, docs
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts",hasSize(2)))
                .andExpect(jsonPath("$.posts[0].title", is("테스트 제목1")))
                .andExpect(jsonPath("$.posts[0].content", is("테스트 본문1")))
                .andExpect(jsonPath("$.posts[0].tags[0]", is("태그1")))
                .andExpect(jsonPath("$.posts[0].nickname", is("테스트1")))
                .andExpect(jsonPath("$.posts[0].score", is(1)))
                .andExpect(jsonPath("$.posts[1].title", is("테스트 제목2")))
                .andExpect(jsonPath("$.posts[1].content", is("테스트 본문2")))
                .andExpect(jsonPath("$.posts[1].tags[0]", is("태그3")))
                .andExpect(jsonPath("$.posts[1].nickname", is("테스트2")))
                .andExpect(jsonPath("$.posts[1].score", is(1)))

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")
                        ),
                        responseFields(
                                fieldWithPath("posts[].title").type(JsonFieldType.STRING).description("게시물 제목"),
                                fieldWithPath("posts[].content").type(JsonFieldType.STRING).description("게시물 본문"),
                                fieldWithPath("posts[].tags[]").type(JsonFieldType.ARRAY).description("게시물 태그들"),
                                fieldWithPath("posts[].nickname").type(JsonFieldType.STRING).description("게시물 작성자 닉네임"),
                                fieldWithPath("posts[].createdAt").type(JsonFieldType.STRING).description("게시물 작성일 (yyyy-MM-dd HH:mm:ss)"),
                                fieldWithPath("posts[].score").type(JsonFieldType.NUMBER).description("게시물 작성자 점수")
                        )
                ));

    }


    private static List<PostInfo> createPostInfo(){
        return List.of(PostInfo.builder()
                .title("테스트 제목1")
                .content("테스트 본문1")
                .tags(List.of("태그1","태그2"))
                .nickname("테스트1")
                .score(1)
                .createdAt(LocalDateTime.now())
                .build(),
                PostInfo.builder()
                        .title("테스트 제목2")
                        .content("테스트 본문2")
                        .tags(List.of("태그3","태그4"))
                        .nickname("테스트2")
                        .score(1)
                        .createdAt(LocalDateTime.now())
                        .build());
    }

}
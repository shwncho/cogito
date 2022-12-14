package com.server.cogito.post.controller;

import com.server.cogito.comment.dto.response.CommentResponse;
import com.server.cogito.common.exception.post.PostErrorCode;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.post.dto.request.PostRequest;
import com.server.cogito.post.dto.request.UpdatePostRequest;
import com.server.cogito.post.dto.response.CreatePostResponse;
import com.server.cogito.post.dto.response.PostInfo;
import com.server.cogito.post.dto.response.PostPageResponse;
import com.server.cogito.post.dto.response.PostResponse;
import com.server.cogito.post.service.PostService;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.server.cogito.support.restdocs.RestDocsConfig.field;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class PostControllerTest extends RestDocsSupport {

    @MockBean
    private PostService postService;

    @Test
    @DisplayName("????????? ?????? ??????")
    void create_post_success() throws Exception{
        //given
        PostRequest request = createPostRequest();
        given(postService.createPost(any(),any())).willReturn(CreatePostResponse.from(1L));

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
                                fieldWithPath("title").type(JsonFieldType.STRING).description("????????? ??????").attributes(field("constraints","null, \" \", \"\" ??????")),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????").attributes(field("constraints","null,  \" \", \"\"??????")),
                                fieldWithPath("files[]").type(JsonFieldType.ARRAY).optional().description("????????? ????????? url"),
                                fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("????????? ????????? id")
                        )
                ));



    }

    private static PostRequest createPostRequest() {
        return PostRequest.builder()
                .title("?????????")
                .content("?????????")
                .files(List.of("file1","file2"))
                .tags(List.of("tag1","tag2"))
                .build();
    }

    private static PostRequest createPostNullRequest() {
        return PostRequest.builder()
                .title(null)
                .content(null)
                .files(List.of("file1","file2"))
                .tags(List.of("tag1","tag2"))
                .build();

    }

    @Test
    @DisplayName("????????? ?????? ?????? / ?????? ????????? ?????? ??????")
    void create_post_fail_not_valid() throws Exception{

        //given
        PostRequest request = createPostNullRequest();


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
    @DisplayName("????????? ????????? ?????? ?????? / ?????????")
    void get_posts_success_latest() throws Exception {

        //given
        PostPageResponse response = PostPageResponse.from(getPostInfo());
        given(postService.getPosts(any())).willReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .param("page","1")
                .param("size","15")
                .contentType(MediaType.APPLICATION_JSON));

        //then, docs
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts",hasSize(2)))
                .andExpect(jsonPath("$.posts[0].title", is("????????? ??????1")))
                .andExpect(jsonPath("$.posts[0].content", is("????????? ??????1")))
                .andExpect(jsonPath("$.posts[0].tags[0]", is("??????1")))
                .andExpect(jsonPath("$.posts[0].nickname", is("?????????1")))
                .andExpect(jsonPath("$.posts[0].score", is(1)))
                .andExpect(jsonPath("$.posts[1].title", is("????????? ??????2")))
                .andExpect(jsonPath("$.posts[1].content", is("????????? ??????2")))
                .andExpect(jsonPath("$.posts[1].tags[0]", is("??????3")))
                .andExpect(jsonPath("$.posts[1].nickname", is("?????????2")))
                .andExpect(jsonPath("$.posts[1].score", is(1)))

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("page").optional().description("????????? ?????? (??????????????? 1 ?????????)"),
                                parameterWithName("size").optional().description("????????? ????????? (??????????????? 20???)")
                        ),
                        responseFields(
                                fieldWithPath("posts[].title").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("posts[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("posts[].tags[]").type(JsonFieldType.ARRAY).description("????????? ??????"),
                                fieldWithPath("posts[].nickname").type(JsonFieldType.STRING).description("????????? ????????? ?????????"),
                                fieldWithPath("posts[].createdAt").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("posts[].score").type(JsonFieldType.NUMBER).description("????????? ????????? ??????")
                        )
                ));

    }


    private static List<PostInfo> getPostInfo(){
        return List.of(PostInfo.builder()
                .title("????????? ??????1")
                .content("????????? ??????1")
                .tags(List.of("??????1","??????2"))
                .nickname("?????????1")
                .score(1)
                .createdAt(LocalDateTime.now())
                .build(),
                PostInfo.builder()
                        .title("????????? ??????2")
                        .content("????????? ??????2")
                        .tags(List.of("??????3","??????4"))
                        .nickname("?????????2")
                        .score(1)
                        .createdAt(LocalDateTime.now())
                        .build());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????")
    public void get_post_success() throws Exception {
        //given
        PostResponse response = getPostResponse();
        given(postService.getPost(anyLong())).willReturn(response);
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/posts/{postId}",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title",is("????????? ??????")))
                .andExpect(jsonPath("$.content",is("????????? ??????")))
                .andExpect(jsonPath("$.tags[0]",is("??????1")))
                .andExpect(jsonPath("$.files[0]",is("??????1")))
                .andExpect(jsonPath("$.nickname",is("?????????")))
                .andExpect(jsonPath("$.profileImgUrl",is("testUrl")))
                .andExpect(jsonPath("$.score",is(1)))
                .andExpect(jsonPath("$.createdAt",is(LocalDateTime.of(2022, 1, 5,0,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))))
                .andExpect(jsonPath("$.commentResponses[0].commentId",is(1)))
                .andExpect(jsonPath("$.commentResponses[0].content",is("????????? ??????")))
                .andExpect(jsonPath("$.commentResponses[0].selected",is(0)))
                .andExpect(jsonPath("$.commentResponses[0].likeCnt",is(0)))
                .andExpect(jsonPath("$.commentResponses[0].userId",is(2)))
                .andExpect(jsonPath("$.commentResponses[0].nickname",is("?????????2")))
                .andExpect(jsonPath("$.commentResponses[0].score",is(4)))
                .andExpect(jsonPath("$.commentResponses[0].profileImgUrl",is("testUrl2")))
                .andExpect(jsonPath("$.commentResponses[0].createdAt",is(LocalDateTime.of(2022, 1, 5,0,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))))
                .andExpect(jsonPath("$.commentResponses[0].children[0].commentId",is(2)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].content",is("????????? ?????????")))
                .andExpect(jsonPath("$.commentResponses[0].children[0].selected",is(0)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].likeCnt",is(0)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].userId",is(3)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].nickname",is("?????????3")))
                .andExpect(jsonPath("$.commentResponses[0].children[0].score",is(4)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].profileImgUrl",is("testUrl3")))
                .andExpect(jsonPath("$.commentResponses[0].children[0].createdAt",is(LocalDateTime.of(2022, 1, 5,0,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))))
                .andExpect(jsonPath("$.commentResponses[0].children[0].children",is(empty())))




                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("postId").description("????????? id")
                        ),
                        responseFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("tags[]").type(JsonFieldType.ARRAY).description("????????? ??????"),
                                fieldWithPath("files[]").type(JsonFieldType.ARRAY).description("????????? ?????? URL"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ????????? ?????????"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.STRING).description("????????? ????????? ????????? ????????? URL"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("????????? ????????? score"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("commentResponses[].commentId").type(JsonFieldType.NUMBER).description("?????? id"),
                                fieldWithPath("commentResponses[].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("commentResponses[].selected").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? 0 or 1"),
                                fieldWithPath("commentResponses[].likeCnt").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("commentResponses[].userId").type(JsonFieldType.NUMBER).description("?????? ????????? ?????? id"),
                                fieldWithPath("commentResponses[].nickname").type(JsonFieldType.STRING).description("?????? ????????? ?????????"),
                                fieldWithPath("commentResponses[].score").type(JsonFieldType.NUMBER).description("?????? ????????? score"),
                                fieldWithPath("commentResponses[].profileImgUrl").type(JsonFieldType.STRING).description("?????? ????????? ????????? ????????? URL"),
                                fieldWithPath("commentResponses[].createdAt").type(JsonFieldType.STRING).description("?????? ?????? ??????"),
                                fieldWithPath("commentResponses[].children[]").type(JsonFieldType.ARRAY).description("???????????? ???????????? ??? ?????????"),
                                fieldWithPath("commentResponses[].children[].commentId").type(JsonFieldType.NUMBER).description("????????? id"),
                                fieldWithPath("commentResponses[].children[].content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("commentResponses[].children[].selected").type(JsonFieldType.NUMBER).description("???????????? ?????? ??????"),
                                fieldWithPath("commentResponses[].children[].likeCnt").type(JsonFieldType.NUMBER).description("???????????? ????????? ??????"),
                                fieldWithPath("commentResponses[].children[].userId").type(JsonFieldType.NUMBER).description("????????? ????????? ?????? id"),
                                fieldWithPath("commentResponses[].children[].nickname").type(JsonFieldType.STRING).description("????????? ????????? ?????????"),
                                fieldWithPath("commentResponses[].children[].score").type(JsonFieldType.NUMBER).description("????????? ????????? score"),
                                fieldWithPath("commentResponses[].children[].profileImgUrl").type(JsonFieldType.STRING).description("????????? ????????? ????????? ????????? URL"),
                                fieldWithPath("commentResponses[].children[].createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("commentResponses[].children[].children[]").type(JsonFieldType.ARRAY).description("???????????? children[]??? ????????? ??? ?????????")
                        )
                ));
    }



    private PostResponse getPostResponse(){
        return PostResponse.builder()
                .title("????????? ??????")
                .content("????????? ??????")
                .tags(List.of("??????1"))
                .files(List.of("??????1"))
                .nickname("?????????")
                .profileImgUrl("testUrl")
                .score(1)
                .createdAt(LocalDateTime.of(2022, 1, 5,0,0,0))
                .commentResponses(List.of(getCommentResponse()))
                .build();

    }

    private CommentResponse getCommentResponse(){
        return CommentResponse.builder()
                .commentId(1L)
                .content("????????? ??????")
                .selected(0)
                .likeCnt(0)
                .userId(2L)
                .nickname("?????????2")
                .score(4)
                .profileImgUrl("testUrl2")
                .createdAt(LocalDateTime.of(2022, 1, 5,0,0,0))
                .children(List.of(CommentResponse.builder()
                        .commentId(2L)
                        .content("????????? ?????????")
                        .selected(0)
                        .likeCnt(0)
                        .userId(3L)
                        .nickname("?????????3")
                        .score(4)
                        .profileImgUrl("testUrl3")
                        .createdAt(LocalDateTime.of(2022, 1, 5,0,0,0))
                        .build()))
                .build();
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? / ???????????? ?????? ?????????")
    public void get_post_fail_not_found() throws Exception {
        //given
        willThrow(new PostNotFoundException()).given(postService).getPost(any());
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/posts/{postId}",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(PostErrorCode.POST_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(PostErrorCode.POST_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void update_post_success() throws Exception {
        //given
        UpdatePostRequest request = createUpdatePostRequest();
        willDoNothing().given(postService).updatePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}",1L)
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
                                parameterWithName("postId").description("????????? id")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).optional().description("????????? ??????").attributes(field("constraints","null or ????????? ????????? ?????? ????????? ??????")),
                                fieldWithPath("content").type(JsonFieldType.STRING).optional().description("????????? ??????").attributes(field("constraints","null ????????? ?????? ????????? ??????")),
                                fieldWithPath("files[]").type(JsonFieldType.ARRAY).optional().description("????????? ????????? url").attributes(field("constraints","????????? ????????? ?????? ????????????, ????????? ????????? ????????? ????????????")),
                                fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("????????? ??????").attributes(field("constraints","????????? ????????? ?????? ????????????, ????????? ????????? ????????? ????????????"))
                        )
                ));
    }

    private UpdatePostRequest createUpdatePostRequest(){
        return UpdatePostRequest.builder()
                .title("?????? ??????")
                .content("?????? ??????")
                .files(List.of("?????? ??????1"))
                .tags(List.of("?????? ??????1"))
                .build();
    }

    @Test
    @DisplayName("????????? ?????? ?????? / ???????????? ?????? ?????????")
    public void update_post_fail_not_found() throws Exception {
        //given
        UpdatePostRequest request = createUpdatePostRequest();
        willThrow(new PostNotFoundException()).given(postService).updatePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}",1L)
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
    @DisplayName("????????? ?????? ??????")
    public void delete_post_success() throws Exception {
        //given
        willDoNothing().given(postService).deletePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/status",1L)
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
                                parameterWithName("postId").description("????????? id")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????? ?????? / ???????????? ?????? ?????????")
    public void delete_post_fail_not_found() throws Exception {
        //given
        willThrow(new PostNotFoundException()).given(postService).deletePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/status",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(PostErrorCode.POST_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(PostErrorCode.POST_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("????????? ?????? ?????? / ???????????? ?????? ??????")
    public void delete_post_fail_invalid_user() throws Exception {
        //given
        willThrow(new UserInvalidException(UserErrorCode.USER_INVALID))
                .given(postService).deletePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/status",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_INVALID.getMessage())));
    }
}
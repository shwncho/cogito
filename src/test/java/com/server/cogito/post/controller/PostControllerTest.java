package com.server.cogito.post.controller;

import com.server.cogito.comment.dto.response.CommentResponse;
import com.server.cogito.common.exception.post.PostErrorCode;
import com.server.cogito.common.exception.post.PostNotFoundException;
import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.common.exception.user.UserNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @DisplayName("게시물 생성 성공")
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

    private static PostRequest createPostRequest() {
        return PostRequest.builder()
                .title("테스트")
                .content("테스트")
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
    @DisplayName("게시물 생성 실패 / 입력 조건에 대한 예외")
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
    @DisplayName("게시물 생성 실패 / 존재하지 않는 유저")
    public void create_post_fail_not_found_user() throws Exception {
        //given
        PostRequest request = createPostRequest();
        given(postService.createPost(any(),any()))
                .willThrow(new UserNotFoundException());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/posts")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("게시물 리스트 조회 성공 / 검색 조건 없을 경우")
    void get_posts_success_latest() throws Exception {

        //given
        PostPageResponse response = PostPageResponse.of(getPostWithoutConditions(),2);
        given(postService.getPosts(any(),any())).willReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .param("page","0")
                .param("size","15")
                .contentType(MediaType.APPLICATION_JSON));

        //then, docs
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].postId",is(1)))
                .andExpect(jsonPath("$.posts[0].title", is("test")))
                .andExpect(jsonPath("$.posts[0].content", is("테스트 본문1")))
                .andExpect(jsonPath("$.posts[0].tags[0]", is("태그1")))
                .andExpect(jsonPath("$.posts[0].nickname", is("테스트1")))
                .andExpect(jsonPath("$.posts[0].profileImgUrl",is("url")))
                .andExpect(jsonPath("$.posts[0].score", is(1)))
                .andExpect(jsonPath("$.posts[0].commentCnt",is(0)))
                .andExpect(jsonPath("$.posts[0].likeCnt",is(0)))
                .andExpect(jsonPath("$.posts[1].postId",is(2)))
                .andExpect(jsonPath("$.posts[1].title", is("테스트 제목2")))
                .andExpect(jsonPath("$.posts[1].content", is("테스트 본문2")))
                .andExpect(jsonPath("$.posts[1].tags[0]", is("태그3")))
                .andExpect(jsonPath("$.posts[1].nickname", is("테스트2")))
                .andExpect(jsonPath("$.posts[1].profileImgUrl",is("url")))
                .andExpect(jsonPath("$.posts[1].score", is(1)))
                .andExpect(jsonPath("$.posts[1].commentCnt",is(0)))
                .andExpect(jsonPath("$.posts[1].likeCnt",is(0)))
                .andExpect(jsonPath("$.total",is(2)))

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("query").optional().description("검색 키워드"),
                                parameterWithName("page").description("페이지 번호 (0페이지 부터)"),
                                parameterWithName("size").description("페이지 사이즈 ")
                        ),
                        responseFields(
                                fieldWithPath("posts[].postId").type(JsonFieldType.NUMBER).description("게시물 id"),
                                fieldWithPath("posts[].title").type(JsonFieldType.STRING).description("게시물 제목"),
                                fieldWithPath("posts[].content").type(JsonFieldType.STRING).description("게시물 본문"),
                                fieldWithPath("posts[].tags[]").type(JsonFieldType.ARRAY).description("게시물 태그"),
                                fieldWithPath("posts[].nickname").type(JsonFieldType.STRING).description("게시물 작성자 닉네임"),
                                fieldWithPath("posts[].profileImgUrl").type(JsonFieldType.STRING).description("게시물 작성자 프로필 이미지 URL"),
                                fieldWithPath("posts[].createdAt").type(JsonFieldType.STRING).description("게시물 작성일"),
                                fieldWithPath("posts[].score").type(JsonFieldType.NUMBER).description("게시물 작성자 점수"),
                                fieldWithPath("posts[].commentCnt").type(JsonFieldType.NUMBER).description("게시물 댓글 개수"),
                                fieldWithPath("posts[].likeCnt").type(JsonFieldType.NUMBER).description("게시물 좋아요 개수"),
                                fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 게시물 개수")

                        )
                ));

    }

    @Test
    @DisplayName("게시물 리스트 조회 성공 / 검색 조건 있을 경우")
    void get_posts_success_query() throws Exception {

        //given
        PostPageResponse response = PostPageResponse.of(getPostWithConditions(),1);
        given(postService.getPosts(any(),any())).willReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .param("query","test")
                .param("page","0")
                .param("size","15")
                .contentType(MediaType.APPLICATION_JSON));

        //then, docs
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].postId",is(1)))
                .andExpect(jsonPath("$.posts[0].title", is("test")))
                .andExpect(jsonPath("$.posts[0].content", is("테스트 본문1")))
                .andExpect(jsonPath("$.posts[0].tags[0]", is("태그1")))
                .andExpect(jsonPath("$.posts[0].nickname", is("테스트1")))
                .andExpect(jsonPath("$.posts[0].profileImgUrl",is("url")))
                .andExpect(jsonPath("$.posts[0].score", is(1)))
                .andExpect(jsonPath("$.posts[0].commentCnt",is(0)))
                .andExpect(jsonPath("$.posts[0].likeCnt",is(0)))
                .andExpect(jsonPath("$.total",is(1)))

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        requestParameters(
                                parameterWithName("query").optional().description("검색 키워드"),
                                parameterWithName("page").description("페이지 번호 (0페이지 부터)"),
                                parameterWithName("size").description("페이지 사이즈 ")
                        ),
                        responseFields(
                                fieldWithPath("posts[].postId").type(JsonFieldType.NUMBER).description("게시물 id"),
                                fieldWithPath("posts[].title").type(JsonFieldType.STRING).description("게시물 제목"),
                                fieldWithPath("posts[].content").type(JsonFieldType.STRING).description("게시물 본문"),
                                fieldWithPath("posts[].tags[]").type(JsonFieldType.ARRAY).description("게시물 태그"),
                                fieldWithPath("posts[].nickname").type(JsonFieldType.STRING).description("게시물 작성자 닉네임"),
                                fieldWithPath("posts[].profileImgUrl").type(JsonFieldType.STRING).description("게시물 작성자 프로필 이미지 URL"),
                                fieldWithPath("posts[].createdAt").type(JsonFieldType.STRING).description("게시물 작성일"),
                                fieldWithPath("posts[].score").type(JsonFieldType.NUMBER).description("게시물 작성자 점수"),
                                fieldWithPath("posts[].commentCnt").type(JsonFieldType.NUMBER).description("게시물 댓글 개수"),
                                fieldWithPath("posts[].likeCnt").type(JsonFieldType.NUMBER).description("게시물 좋아요 개수"),
                                fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 게시물 개수")
                        )
                ));

    }


    private static List<PostInfo> getPostWithoutConditions(){
        return List.of(PostInfo.builder()
                .postId(1L)
                .title("test")
                .content("테스트 본문1")
                .tags(List.of("태그1","태그2"))
                .nickname("테스트1")
                .profileImgUrl("url")
                .score(1)
                .commentCnt(0)
                .likeCnt(0)
                .createdAt(LocalDateTime.now())
                .build(),
                PostInfo.builder()
                        .postId(2L)
                        .title("테스트 제목2")
                        .content("테스트 본문2")
                        .tags(List.of("태그3","태그4"))
                        .nickname("테스트2")
                        .profileImgUrl("url")
                        .score(1)
                        .commentCnt(0)
                        .likeCnt(0)
                        .createdAt(LocalDateTime.now())
                        .build());
    }

    private static List<PostInfo> getPostWithConditions(){
        return List.of(PostInfo.builder()
                        .postId(1L)
                        .title("test")
                        .content("테스트 본문1")
                        .tags(List.of("태그1","태그2"))
                        .nickname("테스트1")
                        .profileImgUrl("url")
                        .score(1)
                        .commentCnt(0)
                        .likeCnt(0)
                        .createdAt(LocalDateTime.now())
                        .build());
    }

    @Test
    @DisplayName("게시물 단건 조회 성공")
    public void get_post_success() throws Exception {
        //given
        PostResponse response = getPostResponse();
        given(postService.getPost(any(),anyLong())).willReturn(response);
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/posts/{postId}",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId",is(1)))
                .andExpect(jsonPath("$.title",is("테스트 제목")))
                .andExpect(jsonPath("$.content",is("테스트 본문")))
                .andExpect(jsonPath("$.tags[0]",is("태그1")))
                .andExpect(jsonPath("$.files[0]",is("파일1")))
                .andExpect(jsonPath("$.nickname",is("테스트")))
                .andExpect(jsonPath("$.profileImgUrl",is("testUrl")))
                .andExpect(jsonPath("$.score",is(1)))
                .andExpect(jsonPath("$.likeCnt",is(0)))
                .andExpect(jsonPath("$.createdAt",is(LocalDateTime.of(2022, 1, 5,0,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))))
                .andExpect(jsonPath("$.isMe",is(true)))
                .andExpect(jsonPath("$.commentResponses[0].commentId",is(1)))
                .andExpect(jsonPath("$.commentResponses[0].content",is("테스트 댓글")))
                .andExpect(jsonPath("$.commentResponses[0].selected",is(0)))
                .andExpect(jsonPath("$.commentResponses[0].likeCnt",is(0)))
                .andExpect(jsonPath("$.commentResponses[0].userId",is(2)))
                .andExpect(jsonPath("$.commentResponses[0].nickname",is("테스트2")))
                .andExpect(jsonPath("$.commentResponses[0].score",is(4)))
                .andExpect(jsonPath("$.commentResponses[0].profileImgUrl",is("testUrl2")))
                .andExpect(jsonPath("$.commentResponses[0].createdAt",is(LocalDateTime.of(2022, 1, 5,0,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))))
                .andExpect(jsonPath("$.commentResponses[0].isMe",is(true)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].commentId",is(2)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].content",is("테스트 대댓글")))
                .andExpect(jsonPath("$.commentResponses[0].children[0].selected",is(0)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].likeCnt",is(0)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].userId",is(3)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].nickname",is("테스트3")))
                .andExpect(jsonPath("$.commentResponses[0].children[0].score",is(4)))
                .andExpect(jsonPath("$.commentResponses[0].children[0].profileImgUrl",is("testUrl3")))
                .andExpect(jsonPath("$.commentResponses[0].children[0].createdAt",is(LocalDateTime.of(2022, 1, 5,0,0,0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))))
                .andExpect(jsonPath("$.commentResponses[0].children[0].children",is(empty())))
                .andExpect(jsonPath("$.commentResponses[0].children[0].isMe",is(true)))




                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("postId").description("게시물 id")
                        ),
                        responseFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시물 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 본문"),
                                fieldWithPath("tags[]").type(JsonFieldType.ARRAY).description("게시물 태그"),
                                fieldWithPath("files[]").type(JsonFieldType.ARRAY).description("게시물 파일 URL"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("게시물 작성자 닉네임"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.STRING).description("게시물 작성자 프로필 이미지 URL"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("게시물 작성자 score"),
                                fieldWithPath("likeCnt").type(JsonFieldType.NUMBER).description("게시물 좋아요 개수"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시물 작성 시간"),
                                fieldWithPath("isMe").type(JsonFieldType.BOOLEAN).description("본인 여부"),
                                fieldWithPath("commentResponses[].commentId").type(JsonFieldType.NUMBER).description("댓글 id"),
                                fieldWithPath("commentResponses[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("commentResponses[].selected").type(JsonFieldType.NUMBER).description("댓글 채택 여부 0 or 1"),
                                fieldWithPath("commentResponses[].likeCnt").type(JsonFieldType.NUMBER).description("댓글 좋아요 개수"),
                                fieldWithPath("commentResponses[].userId").type(JsonFieldType.NUMBER).description("댓글 작성자 유저 id"),
                                fieldWithPath("commentResponses[].nickname").type(JsonFieldType.STRING).description("댓글 작성자 닉네임"),
                                fieldWithPath("commentResponses[].score").type(JsonFieldType.NUMBER).description("댓글 작성자 score"),
                                fieldWithPath("commentResponses[].profileImgUrl").type(JsonFieldType.STRING).description("댓글 작성자 프로필 이미지 URL"),
                                fieldWithPath("commentResponses[].createdAt").type(JsonFieldType.STRING).description("댓글 작성 시간"),
                                fieldWithPath("commentResponses[].children[]").type(JsonFieldType.ARRAY).description("대댓글이 없을경우 빈 리스트"),
                                fieldWithPath("commentResponses[].isMe").type(JsonFieldType.BOOLEAN).description("본인 여부"),
                                fieldWithPath("commentResponses[].children[].commentId").type(JsonFieldType.NUMBER).description("대댓글 id"),
                                fieldWithPath("commentResponses[].children[].content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                fieldWithPath("commentResponses[].children[].selected").type(JsonFieldType.NUMBER).description("대댓글은 채택 없음"),
                                fieldWithPath("commentResponses[].children[].likeCnt").type(JsonFieldType.NUMBER).description("대댓글은 좋아요 없음"),
                                fieldWithPath("commentResponses[].children[].userId").type(JsonFieldType.NUMBER).description("대댓글 작성자 유저 id"),
                                fieldWithPath("commentResponses[].children[].nickname").type(JsonFieldType.STRING).description("대댓글 작성자 닉네임"),
                                fieldWithPath("commentResponses[].children[].score").type(JsonFieldType.NUMBER).description("대댓글 작성자 score"),
                                fieldWithPath("commentResponses[].children[].profileImgUrl").type(JsonFieldType.STRING).description("대댓글 작성자 프로필 이미지 URL"),
                                fieldWithPath("commentResponses[].children[].createdAt").type(JsonFieldType.STRING).description("대댓글 작성 시간"),
                                fieldWithPath("commentResponses[].children[].children[]").type(JsonFieldType.ARRAY).description("대댓글의 children[]은 무조건 빈 리스트"),
                                fieldWithPath("commentResponses[].children[].isMe").type(JsonFieldType.BOOLEAN).description("본인 여부")
                                )
                ));
    }



    private PostResponse getPostResponse(){
        return PostResponse.builder()
                .postId(1L)
                .title("테스트 제목")
                .content("테스트 본문")
                .tags(List.of("태그1"))
                .files(List.of("파일1"))
                .nickname("테스트")
                .profileImgUrl("testUrl")
                .score(1)
                .likeCnt(0)
                .createdAt(LocalDateTime.of(2022, 1, 5,0,0,0))
                .commentResponses(List.of(getCommentResponse()))
                .isMe(true)
                .build();

    }

    private CommentResponse getCommentResponse(){
        return CommentResponse.builder()
                .commentId(1L)
                .content("테스트 댓글")
                .selected(0)
                .likeCnt(0)
                .userId(2L)
                .nickname("테스트2")
                .score(4)
                .profileImgUrl("testUrl2")
                .createdAt(LocalDateTime.of(2022, 1, 5,0,0,0))
                .children(List.of(CommentResponse.builder()
                        .commentId(2L)
                        .content("테스트 대댓글")
                        .selected(0)
                        .likeCnt(0)
                        .userId(3L)
                        .nickname("테스트3")
                        .score(4)
                        .profileImgUrl("testUrl3")
                        .createdAt(LocalDateTime.of(2022, 1, 5,0,0,0))
                        .isMe(true)
                        .build()))
                .isMe(true)
                .build();
    }

    @Test
    @DisplayName("게시물 단건 조회 실패 / 존재하지 않는 게시물")
    public void get_post_fail_not_found() throws Exception {
        //given
        willThrow(new PostNotFoundException()).given(postService).getPost(any(),any());
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
    @DisplayName("게시물 수정 성공")
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
                                parameterWithName("postId").description("게시물 id")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).optional().description("게시물 제목").attributes(field("constraints","null or 공백값 넘길시 기존 제목값 유지")),
                                fieldWithPath("content").type(JsonFieldType.STRING).optional().description("게시물 내용").attributes(field("constraints","null 넘길시 기존 본문값 유지")),
                                fieldWithPath("files[]").type(JsonFieldType.ARRAY).optional().description("게시물 이미지 url").attributes(field("constraints","변화가 없다면 기존 리스트값, 변화가 있다면 새로운 리스트값")),
                                fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("게시물 태그").attributes(field("constraints","변화가 없다면 기존 리스트값, 변화가 있다면 새로운 리스트값"))
                        )
                ));
    }

    private UpdatePostRequest createUpdatePostRequest(){
        return UpdatePostRequest.builder()
                .title("수정 제목")
                .content("수정 본문")
                .files(List.of("수정 파일1"))
                .tags(List.of("수정 태그1"))
                .build();
    }

    @Test
    @DisplayName("게시물 수정 실패 / 존재하지 않는 게시물")
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
    @DisplayName("게시물 삭제 성공")
    public void delete_post_success() throws Exception {
        //given
        willDoNothing().given(postService).deletePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/posts/{postId}",1L)
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
                                parameterWithName("postId").description("게시물 id")
                        )
                ));
    }

    @Test
    @DisplayName("게시물 삭제 실패 / 존재하지 않는 게시물")
    public void delete_post_fail_not_found() throws Exception {
        //given
        willThrow(new PostNotFoundException()).given(postService).deletePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/posts/{postId}",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(PostErrorCode.POST_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(PostErrorCode.POST_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("게시물 삭제 실패 / 유효하지 않은 유저")
    public void delete_post_fail_invalid_user() throws Exception {
        //given
        willThrow(new UserInvalidException(UserErrorCode.USER_INVALID))
                .given(postService).deletePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/posts/{postId}",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_INVALID.getMessage())));
    }

    @Test
    @DisplayName("게시물 좋아요 성공")
    public void like_post_success() throws Exception {
        //given
        willDoNothing().given(postService).likePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/like",1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("postId").description("게시물 id")
                        )
                ));

    }

    @Test
    @DisplayName("게시물 좋아요 실패 / 존재하지 않는 게시물")
    public void like_post_fail_not_found() throws Exception {
        //given
        willThrow(new PostNotFoundException()).given(postService).likePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/like",1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(PostErrorCode.POST_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(PostErrorCode.POST_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("게시물 좋아요 실패 / 유효하지 않은 유저")
    public void like_post_fail_invalid_user() throws Exception {
        //given
        willThrow(new UserInvalidException(UserErrorCode.USER_INVALID))
                .given(postService).likePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/like",1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_INVALID.getMessage())));
    }

    @Test
    @DisplayName("게시물 싫어요 성공")
    public void dislike_post_success() throws Exception {
        //given
        willDoNothing().given(postService).dislikePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/dislike",1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("postId").description("게시물 id")
                        )
                ));

    }

    @Test
    @DisplayName("게시물 싫어요 실패 / 존재하지 않는 게시물")
    public void dislike_post_fail_not_found() throws Exception {
        //given
        willThrow(new PostNotFoundException()).given(postService).dislikePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/dislike",1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(PostErrorCode.POST_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(PostErrorCode.POST_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("게시물 싫어요 실패 / 유효하지 않은 유저")
    public void dislike_post_fail_invalid_user() throws Exception {
        //given
        willThrow(new UserInvalidException(UserErrorCode.USER_INVALID))
                .given(postService).dislikePost(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{postId}/dislike",1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_INVALID.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_INVALID.getMessage())));
    }
}
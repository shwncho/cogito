package com.server.cogito.user.controller;

import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserNicknameExistException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.support.restdocs.RestDocsSupport;
import com.server.cogito.support.security.WithMockJwt;
import com.server.cogito.user.dto.request.UserRequest;
import com.server.cogito.user.dto.response.UserPageResponse;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.user.service.UserService;
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
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class UserControllerTest extends RestDocsSupport {

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("유저 랭킹 조회 성공")
    public void get_users_success() throws Exception {
        //given
        User kakaoUser = mockKakaoUser();
        UserPageResponse response = UserPageResponse.of(
                List.of(UserResponse.from(kakaoUser)),
                1
        );
        given(userService.getUsers(any(),any())).willReturn(response);
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/users")
                .param("page","0")
                .param("size","15")
                .param("query","kakao")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[0].userId",is(1)))
                .andExpect(jsonPath("$.users[0].nickname",is("kakao")))
                .andExpect(jsonPath("$.users[0].profileImgUrl",is("url")))
                .andExpect(jsonPath("$.users[0].score",is(1)))
                .andExpect(jsonPath("$.users[0].introduce",is("소개")))
                .andExpect(jsonPath("$.total",is(1)))

                .andDo(restDocs.document(
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 (0페이지 부터)"),
                                parameterWithName("size").description("페이지 사이즈 "),
                                parameterWithName("query").optional().description("유저 닉네임")
                                ),
                        responseFields(
                                fieldWithPath("users[].userId").type(JsonFieldType.NUMBER).description("유저 id"),
                                fieldWithPath("users[].nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("users[].profileImgUrl").type(JsonFieldType.STRING).description("유저 프로필 사진"),
                                fieldWithPath("users[].score").type(JsonFieldType.NUMBER).description("유저 점수"),
                                fieldWithPath("users[].introduce").type(JsonFieldType.STRING).description("유저 소개"),
                                fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 유저 수")
                        )
                ));
    }

    @Test
    @DisplayName("본인 프로필 조회 성공")
    public void get_me_success() throws Exception {
        //given
        User user = mockKakaoUser();
        UserResponse response = UserResponse.from(user);
        given(userService.getMe(any())).willReturn(response);
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/users/me")
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId",is(1)))
                .andExpect(jsonPath("$.nickname",is("kakao")))
                .andExpect(jsonPath("$.profileImgUrl",is("url")))
                .andExpect(jsonPath("$.score",is(1)))
                .andExpect(jsonPath("$.introduce",is("소개")))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.STRING).description("유저 프로필 사진"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("유저 점수"),
                                fieldWithPath("introduce").type(JsonFieldType.STRING).description("유저 소개")
                        )
                ));
    }

    @Test
    @DisplayName("유저 프로필 조회")
    public void get_user_success() throws Exception {
        //given
        User user = mockKakaoUser();
        UserResponse response = UserResponse.from(user);
        given(userService.getUser(any())).willReturn(response);
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/users/{userId}",1L)
                        .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken"));


        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId",is(1)))
                .andExpect(jsonPath("$.nickname",is("kakao")))
                .andExpect(jsonPath("$.profileImgUrl",is("url")))
                .andExpect(jsonPath("$.score",is(1)))
                .andExpect(jsonPath("$.introduce",is("소개")))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("userId").description("유저 id")
                        ),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.STRING).description("유저 프로필 사진"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("유저 점수"),
                                fieldWithPath("introduce").type(JsonFieldType.STRING).description("유저 소개")
                        )
                ));



    }

    private User mockKakaoUser(){
        return User.builder()
                .id(1L)
                .email("kakao@kakao.com")
                .nickname("kakao")
                .profileImgUrl("url")
                .introduce("소개")
                .provider(Provider.KAKAO)
                .build();
    }

    private User mockGithubUser(){
        return User.builder()
                .id(2L)
                .email("github@github.com")
                .nickname("github")
                .profileImgUrl("url")
                .introduce("소개")
                .provider(Provider.GITHUB)
                .build();
    }

    @Test
    @DisplayName("유저 프로필 수정 성공")
    public void update_user_success() throws Exception {
        //given
        UserRequest request = UserRequest.builder()
                .nickname("수정")
                .profileImgUrl("수정")
                .introduce("수정")
                .build();
        willDoNothing().given(userService).updateUser(any(),any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/users/{userId}",1L)
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
                                parameterWithName("userId").description("유저 id")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("변경할 닉네임"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.STRING).optional().description("변경할 프로필 사진 URL"),
                                fieldWithPath("introduce").type(JsonFieldType.STRING).optional().description("변경할 유저 소개")
                        )
                ));
    }

    @Test
    @DisplayName("유저 프로필 수정 실패 / 이미 존재하는 닉네임")
    public void update_user_fail_exist_user_nickname() throws Exception {
        //given
        UserRequest request = UserRequest.builder()
                .nickname("수정")
                .profileImgUrl("수정")
                .introduce("수정")
                .build();
        willThrow(new UserNicknameExistException(UserErrorCode.USER_NICKNAME_EXIST)).given(userService).updateUser(any(),any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/users/{userId}",1L)
                .header(HttpHeaders.AUTHORIZATION,"Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(UserErrorCode.USER_NICKNAME_EXIST.getCode())))
                .andExpect(jsonPath("$.message",is(UserErrorCode.USER_NICKNAME_EXIST.getMessage())));
    }
}
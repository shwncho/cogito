package com.server.cogito.user.controller;

import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserNicknameExistException;
import com.server.cogito.support.restdocs.RestDocsSupport;
import com.server.cogito.support.security.WithMockJwt;
import com.server.cogito.user.dto.request.UserRequest;
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


import static com.server.cogito.support.restdocs.RestDocsConfig.field;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class UserControllerTest extends RestDocsSupport {

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("유저 프로필 조회")
    public void getMe_success() throws Exception {
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
                .andExpect(jsonPath("$.nickname",is("kakao")))
                .andExpect(jsonPath("$.profileImgUrl").value(nullValue()))
                .andExpect(jsonPath("$.score",is(1)))
                .andExpect(jsonPath("$.introduce").value(nullValue()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        responseFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.NULL).description("유저 프로필 사진"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("유저 점수"),
                                fieldWithPath("introduce").type(JsonFieldType.NULL).description("유저 소개")
                        )
                ));



    }

    private User mockKakaoUser(){
        return User.builder()
                .email("kakao@kakao.com")
                .nickname("kakao")
                .provider(Provider.KAKAO)
                .build();
    }

    @Test
    @DisplayName("유저 프로필 수정 성공")
    public void updateMe_success() throws Exception {
        //given
        UserRequest request = UserRequest.builder()
                .nickname("수정")
                .profileImgUrl("수정")
                .introduce("수정")
                .build();
        willDoNothing().given(userService).updateMe(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/users/me")
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
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.STRING).description("변경할 프로필 사진 URL"),
                                fieldWithPath("introduce").type(JsonFieldType.STRING).description("변경할 유저 소개")
                        )
                ));
    }

    @Test
    @DisplayName("유저 프로필 수정 실패 / 이미 존재하는 닉네임")
    public void updateMe_fail_exist_user_nickname() throws Exception {
        //given
        UserRequest request = UserRequest.builder()
                .nickname("수정")
                .profileImgUrl("수정")
                .introduce("수정")
                .build();
        willThrow(new UserNicknameExistException(UserErrorCode.USER_NICKNAME_EXIST)).given(userService).updateMe(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/users/me")
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
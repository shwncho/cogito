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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class UserControllerTest extends RestDocsSupport {

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("?????? ????????? ??????")
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
                .andExpect(jsonPath("$.nickname",is("kakao")))
                .andExpect(jsonPath("$.profileImgUrl").value(nullValue()))
                .andExpect(jsonPath("$.score",is(1)))
                .andExpect(jsonPath("$.introduce").value(nullValue()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))
                        ),
                        pathParameters(
                                parameterWithName("userId").description("?????? id")
                        ),
                        responseFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.NULL).description("?????? ????????? ??????"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("introduce").type(JsonFieldType.NULL).description("?????? ??????")
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
    @DisplayName("?????? ????????? ?????? ??????")
    public void update_user_success() throws Exception {
        //given
        UserRequest request = UserRequest.builder()
                .nickname("??????")
                .profileImgUrl("??????")
                .introduce("??????")
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
                                parameterWithName("userId").description("?????? id")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).optional().description("????????? ?????????"),
                                fieldWithPath("profileImgUrl").type(JsonFieldType.STRING).optional().description("????????? ????????? ?????? URL"),
                                fieldWithPath("introduce").type(JsonFieldType.STRING).optional().description("????????? ?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? / ?????? ???????????? ?????????")
    public void update_user_fail_exist_user_nickname() throws Exception {
        //given
        UserRequest request = UserRequest.builder()
                .nickname("??????")
                .profileImgUrl("??????")
                .introduce("??????")
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
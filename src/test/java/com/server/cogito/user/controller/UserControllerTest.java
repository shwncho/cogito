package com.server.cogito.user.controller;

import com.server.cogito.support.restdocs.RestDocsSupport;
import com.server.cogito.support.security.WithMockJwt;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.enums.Provider;
import com.server.cogito.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

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
        ResultActions resultActions = mockMvc.perform(
                get("/api/users/me"));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname",is("kakao")))
                .andExpect(jsonPath("$.profileImgUrl").value(nullValue()))
                .andExpect(jsonPath("$.score",is(1)))
                .andExpect(jsonPath("$.introduce").value(nullValue()))
                .andDo(restDocs.document(
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
}
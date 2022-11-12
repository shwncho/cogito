package com.server.cogito.controller;

import com.server.cogito.domain.auth.AuthController;
import com.server.cogito.domain.auth.AuthService;
import com.server.cogito.domain.auth.dto.request.SignInRequest;
import com.server.cogito.domain.auth.dto.response.SignInResponse;
import com.server.cogito.support.restdocs.RestDocsSupport;
import com.server.cogito.support.security.WithMockJwt;
import com.server.cogito.domain.user.UserRepository;
import com.server.cogito.domain.user.domain.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;


@WebMvcTest(AuthController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
public class AuthControllerTest extends RestDocsSupport{

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;


    @Test
    @DisplayName("로그인 성공")
    public void signIn_success() throws Exception {
        //given
        SignInRequest request = SignInRequest.builder()
                .token("KaKaoToken")
                .provider(Provider.KAKAO)
                .build();

        SignInResponse response = SignInResponse.builder()
                .userId(1L)
                .accessToken("testAccessToken")
                .refreshToken("testRefreshToken")
                .build();

        when(authService.signIn(any()))
                .thenReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId",is(1)))
                .andExpect(jsonPath("$.accessToken",is("testAccessToken")))
                .andExpect(jsonPath("$.refreshToken",is("testRefreshToken")))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("token").type(JsonFieldType.STRING).description("oauth 요청 토큰"),
                                fieldWithPath("provider").type(JsonFieldType.STRING).description("oauth 주체")
                        ),
                        responseFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("회원가입 완료된 유저 idx"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("JWT Access Token"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("JWT Refresh Token")
                        )
                ));
    }
}

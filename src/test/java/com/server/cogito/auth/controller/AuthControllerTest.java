package com.server.cogito.auth.controller;

import com.server.cogito.auth.controller.AuthController;
import com.server.cogito.auth.dto.TokenResponse;
import com.server.cogito.auth.service.AuthService;
import com.server.cogito.common.security.AuthUser;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class AuthControllerTest extends RestDocsSupport{

    @MockBean
    private AuthService authService;


//    @Test
//    @DisplayName("로그인 성공")
//    public void login_success() throws Exception {
//        //given
//        SignInRequest request = SignInRequest.builder()
//                .accessToken("oauthToken")
//                .provider("KAKAO")
//                .build();
//
//        TokenResponse response = TokenResponse.builder()
//                .accessToken("testAccessToken")
//                .refreshToken("testRefreshToken")
//                .build();
//
//        when(authService.signIn(any()))
//                .thenReturn(response);
//
//        //when
//        ResultActions resultActions = mockMvc.perform(
//                post("/api/auth/sign-in")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken",is("testAccessToken")))
//                .andExpect(jsonPath("$.refreshToken",is("testRefreshToken")))
//                .andDo(restDocs.document(
//                        requestFields(
//                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("oauth 요청 토큰"),
//                                fieldWithPath("provider").type(JsonFieldType.STRING).description("oauth 주체, ex) KAKAO")
//                        ),
//                        responseFields(
//                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("JWT Access Token"),
//                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("JWT Refresh Token")
//                        )
//                ));
//    }

    //로그인 실패 없는 provider



    @Test
    @DisplayName("로그아웃 성공")
    public void logout_success() throws Exception{

        //given
        String accessToken = "Bearer testAccessToken";

        //expected, docs
        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token").attributes(field("constraints", "JWT Access Token With Bearer"))

                        )
                ));
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    public void reissue_success() throws Exception{

        //given
        String refreshToken = "Bearer testRefreshToken";
        when(authService.reissue(any(AuthUser.class),any()))
                .thenReturn(TokenResponse.builder()
                        .accessToken("testAccessToken")
                        .refreshToken("testRefreshToken")
                        .build());

        //expected, docs
        mockMvc.perform(post("/api/auth/reissue")
                .header(HttpHeaders.AUTHORIZATION, refreshToken))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT Access Token이 만료되었으므로 refreshToken을 전달").attributes(field("constraints", "JWT Refresh Token With Bearer"))
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("JWT Access Token"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("JWT Refresh Token")
                        )
                ));
    }
}

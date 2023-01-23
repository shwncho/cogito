package com.server.cogito.auth.controller;

import com.server.cogito.auth.dto.response.LoginResponse;
import com.server.cogito.auth.dto.response.TokenResponse;
import com.server.cogito.auth.service.AuthService;
import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.auth.AuthErrorCode;
import com.server.cogito.common.exception.auth.RefreshTokenInvalidException;
import com.server.cogito.common.exception.auth.RefreshTokenNotEqualException;
import com.server.cogito.common.exception.auth.RefreshTokenNotFoundException;
import com.server.cogito.common.exception.infrastructure.InfraErrorCode;
import com.server.cogito.common.exception.infrastructure.NoPublicEmailOnGithubException;
import com.server.cogito.common.exception.infrastructure.UnsupportedOauthProviderException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.support.restdocs.RestDocsSupport;
import com.server.cogito.support.security.WithMockJwt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.server.cogito.support.restdocs.RestDocsConfig.field;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class AuthControllerTest extends RestDocsSupport{

    @MockBean
    private AuthService authService;

    private final String ACCESS_TOKEN = "testAccessToken";
    private final String REFRESH_TOKEN = "testRefreshToken";

    @Test
    @DisplayName("로그인 성공 / kakao")
    public void login_success_kakao() throws Exception {
        //given
        String code = "code";
        String provider = "kakao";
        LoginResponse response = LoginResponse.builder()
                .token(mockJwt())
                .registered(true)
                .build();
        given(authService.login(any(),any()))
                .willReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/auth/{provider}/login/token?code="+code,provider));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token.accessToken",is("testAccessToken")))
                .andExpect(jsonPath("$.token.refreshToken",is("testRefreshToken")))
                .andExpect(jsonPath("$.registered",is(true)))
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("provider").description("oauth provider( kakao or github )"))
                        ,
                        requestParameters(parameterWithName("code").description("Authorization code"))
                        ,
                        responseFields(
                                fieldWithPath("token.accessToken").type(JsonFieldType.STRING).description("JWT Access Token"),
                                fieldWithPath("token.refreshToken").type(JsonFieldType.STRING).description("JWT Refresh Token"),
                                fieldWithPath("registered").type(JsonFieldType.BOOLEAN).description("등록된 유저 true, 최초 로그인 false")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 성공 / github")
    public void login_success_github() throws Exception {
        //given
        String code = "code";
        String provider = "github";
        LoginResponse response = LoginResponse.builder()
                .token(mockJwt())
                .registered(true)
                .build();
        given(authService.login(any(),any()))
                .willReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/auth/{provider}/login/token?code="+code,provider));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token.accessToken",is("testAccessToken")))
                .andExpect(jsonPath("$.token.refreshToken",is("testRefreshToken")))
                .andExpect(jsonPath("$.registered",is(true)))
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("provider").description("oauth provider"))
                        ,
                        requestParameters(parameterWithName("code").description("Authorization code"))
                        ,
                        responseFields(
                                fieldWithPath("token.accessToken").type(JsonFieldType.STRING).description("JWT Access Token"),
                                fieldWithPath("token.refreshToken").type(JsonFieldType.STRING).description("JWT Refresh Token"),
                                fieldWithPath("registered").type(JsonFieldType.BOOLEAN).description("등록된 유저 true, 최초 로그인 false")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 실패 / 제공하지 않는 oauth provider")
    public void login_fail_unSupported_oauth_provider() throws Exception {
        //given
        String code = "code";
        String provider = "naver";
        willThrow(new UnsupportedOauthProviderException()).given(authService).login(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/auth/{provider}/login/token?code="+code,provider));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(InfraErrorCode.INVALID_OAUTH.getCode())))
                .andExpect(jsonPath("$.message",is(InfraErrorCode.INVALID_OAUTH.getMessage())));
    }

    @Test
    @DisplayName("로그인 실패 / 카카오")
    public void login_fail_kakao() throws Exception {
        //given
        String code = "code";
        String provider = "kakao";
        willThrow(new ApplicationException(AuthErrorCode.KAKAO_LOGIN)).given(authService).login(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/auth/{provider}/login/token?code="+code,provider));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(AuthErrorCode.KAKAO_LOGIN.getCode())))
                .andExpect(jsonPath("$.message",is(AuthErrorCode.KAKAO_LOGIN.getMessage())));
    }

    @Test
    @DisplayName("로그인 실패 / 깃허브")
    public void login_fail_github() throws Exception {
        //given
        String code = "code";
        String provider = "github";
        willThrow(new ApplicationException(AuthErrorCode.GITHUB_LOGIN)).given(authService).login(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/auth/{provider}/login/token?code="+code,provider));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(AuthErrorCode.GITHUB_LOGIN.getCode())))
                .andExpect(jsonPath("$.message",is(AuthErrorCode.GITHUB_LOGIN.getMessage())));
    }

    @Test
    @DisplayName("로그인 실패 / 깃허브에 public email이 등록되어있지 않을경우")
    public void login_fail_github_no_public_email() throws Exception {
        //given
        String code = "code";
        String provider = "github";
        willThrow(new NoPublicEmailOnGithubException()).given(authService).login(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/auth/{provider}/login/token?code="+code,provider));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(InfraErrorCode.EMPTY_PUBLIC_EMAIL.getCode())))
                .andExpect(jsonPath("$.message",is(InfraErrorCode.EMPTY_PUBLIC_EMAIL.getMessage())));
    }



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
        given(authService.reissue(any(AuthUser.class),any()))
                .willReturn(mockJwt());

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

    @Test
    @DisplayName("토큰 재발급 실패 / 유효하지 않은 refreshToken")
    public void reissue_fail_invalid_refreshToken() throws Exception {
        //given
        String refreshToken = "Bearer testRefreshToken";
        willThrow(new RefreshTokenInvalidException()).given(authService).reissue(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/reissue")
                .header(HttpHeaders.AUTHORIZATION, refreshToken));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(AuthErrorCode.INVALID_REFRESH_TOKEN.getCode())))
                .andExpect(jsonPath("$.message",is(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage())));
    }

    @Test
    @DisplayName("토큰 재발급 실패 / 존재하지 않는 refreshToken")
    public void reissue_fail_not_found_refreshToken() throws Exception {
        //given
        String refreshToken = "Bearer testRefreshToken";
        willThrow(new RefreshTokenNotFoundException()).given(authService).reissue(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/reissue")
                .header(HttpHeaders.AUTHORIZATION, refreshToken));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN.getCode())))
                .andExpect(jsonPath("$.message",is(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage())));
    }

    @Test
    @DisplayName("토큰 재발급 실패 / 동일하지 않는 refreshToken")
    public void reissue_fail_not_equal_refreshToken() throws Exception {
        //given
        String refreshToken = "Bearer testRefreshToken";
        willThrow(new RefreshTokenNotEqualException()).given(authService).reissue(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/reissue")
                .header(HttpHeaders.AUTHORIZATION, refreshToken));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(AuthErrorCode.NOT_EQUAL_REFRESH_TOKEN.getCode())))
                .andExpect(jsonPath("$.message",is(AuthErrorCode.NOT_EQUAL_REFRESH_TOKEN.getMessage())));
    }

    private TokenResponse mockJwt(){
        return TokenResponse.builder()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }

}

package com.server.cogito.auth.controller;

import com.server.cogito.auth.dto.response.ReissueTokenResponse;
import com.server.cogito.auth.dto.result.LoginResult;
import com.server.cogito.auth.service.AuthService;
import com.server.cogito.auth.service.RefreshTokenCookieProvider;
import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.auth.AuthErrorCode;
import com.server.cogito.common.exception.auth.RefreshTokenInvalidException;
import com.server.cogito.common.exception.auth.RefreshTokenNotFoundException;
import com.server.cogito.common.exception.infrastructure.InfraErrorCode;
import com.server.cogito.common.exception.infrastructure.NoPublicEmailOnGithubException;
import com.server.cogito.common.exception.infrastructure.UnsupportedOauthProviderException;
import com.server.cogito.support.restdocs.RestDocsSupport;
import com.server.cogito.support.security.WithMockJwt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.server.cogito.support.restdocs.RestDocsConfig.field;


@WebMvcTest(AuthController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockJwt
class AuthControllerTest extends RestDocsSupport{

    @MockBean
    private AuthService authService;

    @MockBean
    private RefreshTokenCookieProvider refreshTokenCookieProvider;

    private final String ACCESS_TOKEN = "testAccessToken";
    private final String REFRESH_TOKEN = "testRefreshToken";
    private final String RENEWAL_ACCESS_TOKEN = "refreshedTestAccessToken";
    private final String RENEWAL_REFRESH_TOKEN = "refreshedTestRefreshToken";

    @Test
    @DisplayName("로그인 성공 / kakao")
    public void login_success_kakao() throws Exception {
        //given
        String code = "code";
        String provider = "kakao";
        LoginResult result = LoginResult.of(ACCESS_TOKEN,REFRESH_TOKEN,true);
        ResponseCookie cookie = createCookie(REFRESH_TOKEN);
        given(authService.login(any(),any()))
                .willReturn(result);
        given(refreshTokenCookieProvider.createCookie(REFRESH_TOKEN))
                .willReturn(cookie);

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/auth/{provider}/login/token?code="+code,provider));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("Set-cookie", containsString("refreshToken=" + REFRESH_TOKEN)))
                .andExpect(jsonPath("$.accessToken",is("testAccessToken")))
                .andExpect(jsonPath("$.registered",is(true)))
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("provider").description("oauth provider( kakao or github )"))
                        ,
                        requestParameters(parameterWithName("code").description("Authorization code"))
                        ,
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("JWT Refresh Token")
                        )
                        ,
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("JWT Access Token"),
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
        LoginResult result = LoginResult.of(ACCESS_TOKEN,REFRESH_TOKEN,true);
        ResponseCookie cookie = createCookie(REFRESH_TOKEN);
        given(authService.login(any(),any()))
                .willReturn(result);
        given(refreshTokenCookieProvider.createCookie(REFRESH_TOKEN))
                .willReturn(cookie);

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/auth/{provider}/login/token?code="+code,provider));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("Set-cookie", containsString("refreshToken=" + REFRESH_TOKEN)))
                .andExpect(jsonPath("$.accessToken",is("testAccessToken")))
                .andExpect(jsonPath("$.registered",is(true)))
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("provider").description("oauth provider( kakao or github )"))
                        ,
                        requestParameters(parameterWithName("code").description("Authorization code"))
                        ,
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("JWT Refresh Token")
                        )
                        ,
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("JWT Access Token"),
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
        willDoNothing().given(authService).logout(any(),any());

        //expected, docs
        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .cookie(new Cookie("refreshToken", REFRESH_TOKEN)))

                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    public void reissue_success() throws Exception{

        //given
        ReissueTokenResponse response = ReissueTokenResponse.of(RENEWAL_ACCESS_TOKEN,RENEWAL_REFRESH_TOKEN);
        ResponseCookie cookie = createCookie(RENEWAL_REFRESH_TOKEN);
        given(refreshTokenCookieProvider.createCookie(RENEWAL_REFRESH_TOKEN))
                .willReturn(cookie);
        given(authService.reissue(any(),any()))
                .willReturn(response);

        //expected, docs
        mockMvc.perform(post("/api/auth/reissue")
                .cookie(new Cookie("refreshToken", REFRESH_TOKEN)))

                .andExpect(status().isOk())
                .andExpect(cookie().value("refreshToken", RENEWAL_REFRESH_TOKEN))
                .andExpect(jsonPath("$.accessToken",is(RENEWAL_ACCESS_TOKEN)));
    }


    @Test
    @DisplayName("토큰 재발급 실패 / 존재하지 않는 refreshToken")
    public void reissue_fail_not_found_refreshToken() throws Exception {
        //given
        willThrow(new RefreshTokenNotFoundException()).given(authService).reissue(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/reissue")
                .cookie(new Cookie("refreshToken", REFRESH_TOKEN)));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN.getCode())))
                .andExpect(jsonPath("$.message",is(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage())));
    }

    @Test
    @DisplayName("토큰 재발급 실패 / 유효하지 않은 refreshToken")
    public void reissue_fail_invalid_refreshToken() throws Exception {
        //given
        willThrow(new RefreshTokenInvalidException()).given(authService).reissue(any(),any());
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/reissue")
                .cookie(new Cookie("refreshToken", REFRESH_TOKEN)));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code",is(AuthErrorCode.INVALID_REFRESH_TOKEN.getCode())))
                .andExpect(jsonPath("$.message",is(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage())));
    }

    private ResponseCookie createCookie(String value){
        return ResponseCookie.from("refreshToken",value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1000*60*60*4L)
                .sameSite(org.springframework.boot.web.server.Cookie.SameSite.NONE.attributeValue())
                .build();
    }

}

package com.server.cogito.support.security;

import com.server.cogito.domain.user.domain.Provider;
import com.server.cogito.domain.user.domain.User;
import com.server.cogito.global.common.security.AuthUser;
import com.server.cogito.global.common.security.CustomUserDetailsService;
import com.server.cogito.global.common.security.jwt.JwtAccessDeniedHandler;
import com.server.cogito.global.common.security.jwt.JwtAuthenticationEntryPoint;
import com.server.cogito.global.common.security.jwt.JwtProvider;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class SecuritySupport {

    @MockBean
    protected JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    protected JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @MockBean
    protected CustomUserDetailsService customUserDetailsService;
    @MockBean
    protected JwtProvider jwtProvider;
    @MockBean
    protected RedisTemplate redisTemplate;

    protected void setUpAuthenticated() {
        when(jwtProvider.validateToken(any())).thenReturn(true);
        when(jwtProvider.getUserEmail(any())).thenReturn("test@gmail.com");
        when(customUserDetailsService.loadUserByUsername(any()))
                .thenReturn(AuthUser.of(
                        User.builder()
                                .id(1L)
                                .email("test@gmail.com")
                                .nickname(("테스트"))
                                .provider(Provider.KAKAO)
                                .build()));
    }

}

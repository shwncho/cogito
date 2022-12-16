package com.server.cogito.support.security;

import com.server.cogito.global.common.security.CustomUserDetailsService;
import com.server.cogito.global.common.security.jwt.JwtAccessDeniedHandler;
import com.server.cogito.global.common.security.jwt.JwtAuthenticationEntryPoint;
import com.server.cogito.global.common.security.jwt.JwtProvider;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

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
}

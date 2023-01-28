package com.server.cogito.common.security.jwt;

import com.server.cogito.auth.repository.TokenRepository;
import com.server.cogito.common.exception.auth.TokenException;
import com.server.cogito.common.exception.auth.AuthErrorCode;
import com.server.cogito.common.exception.auth.TokenAuthenticationException;
import com.server.cogito.common.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String jwt = resolveToken(request);
            if (isValid(jwt)) {
                String email = jwtProvider.getUserEmail(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (TokenException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TokenAuthenticationException(AuthErrorCode.UNAUTHORIZED);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && jwtProvider.isStartWithBearer(bearerToken)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isValid(String jwt) {
        return StringUtils.hasText(jwt)
                && jwtProvider.validateToken(jwt)
                && !tokenRepository.existsLogoutAccessTokenById(jwt);
    }
}

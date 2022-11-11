package com.server.cogito.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.cogito.common.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        //필요한 권한이 없이 접근하려 할때 403
        setResponse(response,accessDeniedException);
    }

    private void setResponse(HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error("[AccessDeniedException Exception] {}", accessDeniedException.getMessage());
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String json = objectMapper.writeValueAsString(ErrorResponse.fromForbiddenAtFilter("권한이 없습니다."));
        response.getWriter().write(json);
    }
}

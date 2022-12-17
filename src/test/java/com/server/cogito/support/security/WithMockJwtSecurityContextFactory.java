package com.server.cogito.support.security;

import com.server.cogito.global.common.security.AuthUser;
import com.server.cogito.domain.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;


final class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {

    public SecurityContext createSecurityContext(WithMockJwt annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        AuthUser.of(createUser(annotation.email())),
                        null,
                        createAuthorityList(annotation.authority().toString()));
        context.setAuthentication(authentication);
        return context;
    }

    private User createUser(String email){
        return User.builder()
                .email(email)
                .nickname("심플")
                .build();
    }


}

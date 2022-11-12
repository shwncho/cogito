package com.server.cogito.support.security;

import com.server.cogito.domain.user.domain.Authority;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockJwtSecurityContextFactory.class)
public @interface WithMockJwt {

    long id() default 1L;

    String email() default "test@test.com";

    Authority authority() default Authority.ROLE_USER;

}

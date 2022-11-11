package com.server.cogito.user;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.dto.response.TestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public TestResponse test(@AuthenticationPrincipal AuthUser authUser){
        return userService.test(authUser.getUsername());

    }
}

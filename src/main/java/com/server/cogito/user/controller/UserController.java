package com.server.cogito.user.controller;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal AuthUser authUser){
        return userService.getMe(authUser);
    }
}

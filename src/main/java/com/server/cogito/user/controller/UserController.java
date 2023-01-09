package com.server.cogito.user.controller;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.dto.request.UserRequest;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public void updateUser(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long userId, @RequestBody UserRequest userRequest){
        userService.updateUser(authUser, userId, userRequest);
    }
}

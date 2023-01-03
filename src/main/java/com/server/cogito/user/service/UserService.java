package com.server.cogito.user.service;

import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Transactional(readOnly = true)
    public UserResponse getMe(AuthUser authUser){
        return UserResponse.from(authUser.getUser());
    }
}

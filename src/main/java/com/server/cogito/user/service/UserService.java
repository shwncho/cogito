package com.server.cogito.user.service;

import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserNicknameExistException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.dto.request.UserRequest;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getMe(AuthUser authUser){
        return UserResponse.from(authUser.getUser());
    }

    @Transactional
    public void updateMe(AuthUser authUser, UserRequest userRequest){
        User user = authUser.getUser();
        if(userRepository.existsByNickname(userRequest.getNickname())){
            throw new UserNicknameExistException(UserErrorCode.USER_NICKNAME_EXIST);
        }
        user.change(userRequest);
    }
}

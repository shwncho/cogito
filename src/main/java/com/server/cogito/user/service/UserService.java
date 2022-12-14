package com.server.cogito.user.service;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.common.exception.user.UserInvalidException;
import com.server.cogito.common.exception.user.UserNicknameExistException;
import com.server.cogito.common.exception.user.UserNotFoundException;
import com.server.cogito.common.security.AuthUser;
import com.server.cogito.user.dto.request.UserRequest;
import com.server.cogito.user.dto.response.UserResponse;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId){
        User user = userRepository.findByIdAndStatus(userId, BaseEntity.Status.ACTIVE)
                .orElseThrow(UserNotFoundException::new);
        return UserResponse.from(user);
    }

    @Transactional
    public void updateUser(AuthUser authUser, Long userId, UserRequest userRequest){
        User user = userRepository.findByIdAndStatus(userId, BaseEntity.Status.ACTIVE)
                .orElseThrow(UserNotFoundException::new);
        validateUser(authUser, userRequest, user);
        user.change(userRequest);
    }

    private void validateUser(AuthUser authUser, UserRequest userRequest, User user) {
        if(!Objects.equals(authUser.getUserId(), user.getId())){
            throw new UserInvalidException(UserErrorCode.USER_INVALID);
        }
        if(userRepository.existsByNickname(userRequest.getNickname())){
            throw new UserNicknameExistException(UserErrorCode.USER_NICKNAME_EXIST);
        }
    }
}

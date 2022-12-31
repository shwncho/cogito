package com.server.cogito.common.security;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.user.UserErrorCode;
import com.server.cogito.user.entity.User;
import com.server.cogito.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailAndStatus(email, BaseEntity.Status.ACTIVE)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.USER_NOT_EXIST));
        return AuthUser.of(user);
    }

}
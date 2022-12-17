package com.server.cogito.global.common.security;

import com.server.cogito.global.common.entity.Status;
import com.server.cogito.global.common.exception.ApplicationException;
import com.server.cogito.domain.user.entity.User;
import com.server.cogito.domain.user.repository.UserRepository;
import com.server.cogito.global.common.exception.user.UserErrorCode;
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
        User user = userRepository.findByEmailAndStatus(email, Status.ACTIVE)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.USER_NOT_EXIST));
        return AuthUser.of(user);
    }

}
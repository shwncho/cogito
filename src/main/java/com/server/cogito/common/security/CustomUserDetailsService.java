package com.server.cogito.common.security;

import com.server.cogito.common.entity.Status;
import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.user.domain.User;
import com.server.cogito.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.server.cogito.common.exception.user.UserErrorCode.USER_NOT_EXIST;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailAndStatus(email, Status.ACTIVE)
                .orElseThrow(() -> new ApplicationException(USER_NOT_EXIST));
        return AuthUser.of(user);
    }

}
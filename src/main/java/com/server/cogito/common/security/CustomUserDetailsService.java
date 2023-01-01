package com.server.cogito.common.security;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.common.exception.user.UserNotFoundException;
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
                .orElseThrow(UserNotFoundException::new);
        return AuthUser.of(user);
    }

}
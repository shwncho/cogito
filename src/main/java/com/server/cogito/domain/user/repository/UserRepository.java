package com.server.cogito.domain.user.repository;

import com.server.cogito.global.common.entity.Status;
import com.server.cogito.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(String email, Status status);

}

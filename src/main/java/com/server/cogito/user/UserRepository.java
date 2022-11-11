package com.server.cogito.user;

import com.server.cogito.common.entity.Status;
import com.server.cogito.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(String email, Status status);

}

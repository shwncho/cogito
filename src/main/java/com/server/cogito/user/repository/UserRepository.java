package com.server.cogito.user.repository;

import com.server.cogito.common.entity.BaseEntity;
import com.server.cogito.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByEmailAndStatus(String email, BaseEntity.Status status);
    Optional<User> findByIdAndStatus(Long userId, BaseEntity.Status status);
    boolean existsByNickname(String nickname);
}

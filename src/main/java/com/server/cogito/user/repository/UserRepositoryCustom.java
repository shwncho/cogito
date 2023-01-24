package com.server.cogito.user.repository;

import com.server.cogito.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> findWithSearchConditions(String query, Pageable pageable);

    Page<User> findWithoutSearchConditions(Pageable pageable);
}

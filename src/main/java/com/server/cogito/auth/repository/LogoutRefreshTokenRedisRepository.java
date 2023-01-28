package com.server.cogito.auth.repository;

import com.server.cogito.auth.domain.LogoutRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutRefreshTokenRedisRepository extends CrudRepository<LogoutRefreshToken, String> {
}

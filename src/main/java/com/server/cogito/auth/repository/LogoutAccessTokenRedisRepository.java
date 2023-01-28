package com.server.cogito.auth.repository;

import com.server.cogito.auth.domain.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessToken, String> {
}

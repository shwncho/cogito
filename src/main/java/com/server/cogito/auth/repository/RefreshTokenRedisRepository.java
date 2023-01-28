package com.server.cogito.auth.repository;


import com.server.cogito.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;


public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}

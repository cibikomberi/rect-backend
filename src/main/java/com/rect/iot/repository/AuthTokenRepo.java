package com.rect.iot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.user.AuthToken;

@Repository
public interface AuthTokenRepo extends MongoRepository<AuthToken, String> {
    AuthToken findByToken(String token);
    List<AuthToken> findByUserId(String userId);
}

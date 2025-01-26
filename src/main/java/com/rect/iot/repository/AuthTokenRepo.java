package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.AuthToken;

@Repository
public interface AuthTokenRepo extends MongoRepository<AuthToken, String> {
    AuthToken findByToken(String token);
}

package com.rect.iot.repository;

import com.rect.iot.model.user.AuthToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthTokenRepo extends MongoRepository<AuthToken, String> {
    AuthToken findByToken(String token);
    List<AuthToken> findByUserId(String userId);
    AuthToken deleteByUserIdAndId(String userId, String id);
}

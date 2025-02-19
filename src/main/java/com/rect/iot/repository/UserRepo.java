package com.rect.iot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.user.User;

@Repository
public interface UserRepo extends MongoRepository<User, String>{
    User findByEmail(String email);

    @Query(value = "{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'email': { '$regex': ?0, '$options': 'i' } } ] }")
    List<User> searchUsers(String keyword);
}

package com.rect.iot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{
    User findByUsername(String username);
}

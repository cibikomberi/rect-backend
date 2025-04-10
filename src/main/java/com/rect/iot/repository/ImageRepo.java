package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rect.iot.model.Image;

public interface ImageRepo extends MongoRepository<Image, String> {
    
}

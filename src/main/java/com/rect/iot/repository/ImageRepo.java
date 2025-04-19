package com.rect.iot.repository;

import com.rect.iot.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepo extends MongoRepository<Image, String> {
    
}

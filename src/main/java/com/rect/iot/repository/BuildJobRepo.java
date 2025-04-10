package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.BuildJob;


@Repository
public interface BuildJobRepo extends MongoRepository<BuildJob, String>{
    
}

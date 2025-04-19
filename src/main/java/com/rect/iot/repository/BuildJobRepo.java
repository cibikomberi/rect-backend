package com.rect.iot.repository;

import com.rect.iot.model.BuildJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BuildJobRepo extends MongoRepository<BuildJob, String>{
    
}

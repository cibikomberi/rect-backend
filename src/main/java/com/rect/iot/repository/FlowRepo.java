package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.node.Flow;

@Repository
public interface FlowRepo extends MongoRepository<Flow, String>{
    
}

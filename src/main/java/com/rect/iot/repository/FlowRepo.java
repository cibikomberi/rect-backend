package com.rect.iot.repository;

import com.rect.iot.model.node.Flow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepo extends MongoRepository<Flow, String>{
    
}

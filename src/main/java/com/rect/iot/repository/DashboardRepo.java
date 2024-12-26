package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rect.iot.model.Dashboard;

public interface DashboardRepo extends MongoRepository<Dashboard, String> {
    
}

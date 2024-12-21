package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rect.iot.model.DashboardData;

public interface DashboardDataRepo extends MongoRepository<DashboardData, String> {
    
}

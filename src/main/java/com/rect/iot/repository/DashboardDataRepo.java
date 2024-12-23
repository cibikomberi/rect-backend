package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rect.iot.model.widget.DashboardData;

public interface DashboardDataRepo extends MongoRepository<DashboardData, String> {
    
}

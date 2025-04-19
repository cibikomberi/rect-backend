package com.rect.iot.repository;

import com.rect.iot.model.dashboard.DashboardData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DashboardDataRepo extends MongoRepository<DashboardData, String> {
    
}

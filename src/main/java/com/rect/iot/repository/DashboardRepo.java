package com.rect.iot.repository;

import com.rect.iot.model.dashboard.Dashboard;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DashboardRepo extends MongoRepository<Dashboard, String> {
    List<Dashboard> findByOwner(String owner);
}

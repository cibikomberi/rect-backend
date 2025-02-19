package com.rect.iot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rect.iot.model.dashboard.Dashboard;

public interface DashboardRepo extends MongoRepository<Dashboard, String> {
    List<Dashboard> findByOwner(String owner);
}

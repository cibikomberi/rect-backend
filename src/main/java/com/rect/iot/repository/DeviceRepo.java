package com.rect.iot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.Device;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long> {
    
}

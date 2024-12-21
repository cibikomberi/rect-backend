package com.rect.iot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rect.iot.model.DashboardData;
import com.rect.iot.model.Device;
import com.rect.iot.repository.DashboardDataRepo;
import com.rect.iot.repository.DeviceRepo;

@Service
public class DashboardService {
    @Autowired
    private DashboardDataRepo dashboardDataRepo;
    @Autowired
    private DeviceRepo deviceRepo;

    public DashboardData saveDashboardData(Long deviceId, DashboardData data) {
        DashboardData saved = dashboardDataRepo.save(data);
        Device device = deviceRepo.findById(deviceId).get();
        device.setDashboardId(saved.getId());
        deviceRepo.save(device);
        return saved;
    }

    public DashboardData getDashboardData(String id) {
        return dashboardDataRepo.findById(id).get();
    }
}

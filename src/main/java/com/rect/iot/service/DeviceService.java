package com.rect.iot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.rect.iot.model.Dashboard;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.Template;
import com.rect.iot.model.ThingData;
import com.rect.iot.model.User;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.node.Flow;
import com.rect.iot.model.widget.DashboardData;
import com.rect.iot.model.widget.Widget;
import com.rect.iot.repository.DashboardDataRepo;
import com.rect.iot.repository.DashboardRepo;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.FlowRepo;
import com.rect.iot.repository.TemplateRepo;
import com.rect.iot.repository.ThingDataRepo;
import com.rect.iot.repository.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DeviceService {

    private DeviceRepo deviceRepo;
    private TemplateRepo templateRepo;
    private DeviceMetadataRepo deviceMetadataRepo;
    private DashboardDataRepo dashboardDataRepo;
    private DashboardRepo dashboardRepo;
    private FlowRepo flowRepo;
    private UserService userService;
    private ThingDataRepo thingDataRepo;
    private UserRepo userRepo;

    public List<Device> getMyDevices() {
        String userId = userService.getMyUserId();
        return deviceRepo.findByOwner(userId);
    }

    // TODO: verify template id
    public Device createDevice(String name, String board, String templateId) {
        String userId = userService.getMyUserId();

        DeviceMetadata deviceMetadata = deviceMetadataRepo.save(DeviceMetadata.builder()
                .datastreams(new ArrayList<>())
                .build());

        DashboardData data = DashboardData.builder()
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();
        DashboardData savedDashboardData = dashboardDataRepo.save(data);

        Dashboard savedDashboard = dashboardRepo.save(Dashboard.builder()
                .name(name)
                .access("private")
                .dashboardDataId(savedDashboardData.getId())
                .owner(userId)
                .build());

        Device savedDevice = deviceRepo.save(Device.builder()
                .board(board)
                .name(name)
                .lastActiveTime(LocalDateTime.now())
                .metadataId(deviceMetadata.getId())
                .dashboardId(savedDashboard.getId())
                .templateId(templateId)
                .owner(userId)
                .userAccess(new HashMap<>())
                .build());

        savedDashboard.setIsDeviceSpecific(true);
        savedDashboard.setAssociatedDevices(Collections.singletonList(savedDevice.getId()));
        dashboardRepo.save(savedDashboard);

        return savedDevice;
    }

    public Device getDeviceInfo(String id) throws IllegalAccessException {
        Device device = deviceRepo.findById(id).get();
        String access = getAccessLevel(device);

        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            Template template = templateRepo.findById(device.getTemplateId()).get();
            device.setTemplateName(template.getName());
            return device;
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    // TODO: change in react app
    // TODO: add method to update access and datastream
    public Device updateDeviceInfo(String id, Device newInfo) throws IllegalAccessException {
        Device device = deviceRepo.findById(id).get();
        String access = getAccessLevel(device);
        if (access.equals("Editor") || access.equals("Owner")) {
            device.setName(newInfo.getName());
            device.setDescription(newInfo.getDescription());
            device.setInheritTemplate(newInfo.getInheritTemplate());
            return deviceRepo.save(device);
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public DeviceMetadata getDeviceMetadata(String deviceId) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            metadata.setUserAccess(device.getUserAccess());
            return metadata;
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String addDatastream(String deviceId, Datastream datastream) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();
            if (!existingDatastreams.contains(datastream)) {
                datastream.setDeviceId(deviceId);
                existingDatastreams.add(datastream);
                deviceMetadataRepo.save(metadata);
                return "ok";
            }
            return "Id already exists";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String updateDatastream(String deviceId, String datastreamId, Datastream datastream)
            throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();

            if (!datastreamId.equals(datastream.getIdentifier()) && existingDatastreams.contains(datastream)) {
                return "Id already exists";
            }

            for (int i = 0; i < existingDatastreams.size(); i++) {
                if (existingDatastreams.get(i).getIdentifier().equals(datastreamId)) {

                    if (existingDatastreams.get(i).getIdentifier().equals(datastream.getIdentifier())
                            && existingDatastreams.get(i).getType().equals(datastream.getType())) {
                        datastream.setDeviceId(deviceId);
                        existingDatastreams.set(i, datastream);
                        System.out.println(deviceMetadataRepo.save(metadata));
                        return "ok";
                    } else {
                        List<ThingData<?>> deleted = thingDataRepo.deleteByDeviceIdAndDatastreamId(deviceId,
                                datastreamId);
                        System.out.println("deleted");
                        System.out.println(deleted);
                        datastream.setDeviceId(deviceId);
                        existingDatastreams.set(i, datastream);
                        deviceMetadataRepo.save(metadata);
                        return "ok";
                    }
                }
            }

            return "Invalid id";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String deleteDatastream(String deviceId, String datastreamId) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();
            for (int i = 0; i < existingDatastreams.size(); i++) {
                if (existingDatastreams.get(i).getIdentifier().equals(datastreamId)) {
                    existingDatastreams.remove(i);
                    deviceMetadataRepo.save(metadata);
                    return "ok";
                }
            }
            return "Invalid id";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String updateUserAccess(String deviceId, String userId, String accessLevel) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (accessLevel.equals("Editor") || accessLevel.equals("Viewer")) {
            System.out.println("a");
            if (access.equals("Editor") || access.equals("Owner")) {
                User user = userRepo.findById(userId).get();
                user.getSharedDevices().add(deviceId);
                userRepo.save(user);
                device.getUserAccess().put(userId, accessLevel);
                deviceRepo.save(device);
                return "ok";
            }
            return "fail";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String removeUserAccess(String deviceId, String userId) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            User user = userRepo.findById(userId).get();
            user.getSharedDevices().remove(deviceId);
            userRepo.save(user);
            device.getUserAccess().remove(userId);
            deviceRepo.save(device);
            return "ok";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public List<User> getFriends(String nickname) {
        return userRepo.searchUsers(nickname);
        // return null;
    }

    public Flow saveDeviceFlow(String deviceId, Flow flow) {
        // Get active device
        Device device = deviceRepo.findById(deviceId).get();
        // Set the existing id
        flow.setId(device.getFlowId());
        Flow savedFlow = flowRepo.save(flow);

        // Save the flow
        device.setFlowId(savedFlow.getId());
        deviceRepo.save(device);

        return flow;
    }

    private String getAccessLevel(Device device) {
        String userId = userService.getMyUserId();

        if (device.getOwner().equals(userId)) {
            return "Owner";
        }
        return device.getUserAccess().getOrDefault(userId, "No Access");
    }
}

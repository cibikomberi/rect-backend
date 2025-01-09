package com.rect.iot.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.model.Dashboard;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.DeviceConstants;
import com.rect.iot.model.Image;
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
import com.rect.iot.repository.DeviceConstantsRepo;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.FlowRepo;
import com.rect.iot.repository.ImageRepo;
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
    private ImageRepo imageRepo;
    private DeviceConstantsRepo deviceConstantsRepo;
    private BuildService buildService;

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
                .access("Private")
                .dashboardDataId(savedDashboardData.getId())
                .owner(userId)
                .userAccess(new HashMap<String, String>())
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
        Set<String> associatedDevices = new HashSet<String>();
        associatedDevices.add(savedDevice.getId());
        savedDashboard.setAssociatedDevices(associatedDevices);
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

    // TODO: add method to update access and datastream
    public Device updateDeviceInfo(String id, Device newInfo, MultipartFile multipartImage)
            throws IllegalAccessException, IOException, InterruptedException {
        Device device = deviceRepo.findById(id).get();
        String access = getAccessLevel(device);
        if (access.equals("Editor") || access.equals("Owner")) {
            if (multipartImage != null) {
                Image image = new Image();
                image.setImageType(multipartImage.getContentType());
                image.setContent(multipartImage.getBytes());
                if (device.getImage() != null) {
                    imageRepo.deleteById(device.getImage());
                }
                Image savedImage = imageRepo.save(image);
                device.setImage(savedImage.getId());
            }

            if(newInfo.getInheritTemplate() && device.getInheritTemplate().equals(false)) {
                buildService.buildProject(device.getTemplateId(), device, device.getTargetVersion());
            }
            
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

    public Datastream addDatastream(String deviceId, Datastream datastream) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();
            if (!existingDatastreams.contains(datastream)) {
                datastream.setDeviceId(deviceId);
                existingDatastreams.add(datastream);
                deviceMetadataRepo.save(metadata);
                return datastream;
            }
            throw new IllegalAccessException("Id already exists");
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public Datastream updateDatastream(String deviceId, String datastreamId, Datastream datastream)
            throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();

            if (!datastreamId.equals(datastream.getIdentifier()) && existingDatastreams.contains(datastream)) {
                throw new IllegalAccessException("Id already exists");
            }

            for (int i = 0; i < existingDatastreams.size(); i++) {
                if (existingDatastreams.get(i).getIdentifier().equals(datastreamId)) {

                    if (existingDatastreams.get(i).getIdentifier().equals(datastream.getIdentifier())
                            && existingDatastreams.get(i).getType().equals(datastream.getType())) {
                        datastream.setDeviceId(deviceId);
                        existingDatastreams.set(i, datastream);
                        System.out.println(deviceMetadataRepo.save(metadata));
                        return datastream;
                    } else {
                        List<ThingData<?>> deleted = thingDataRepo.deleteByDeviceIdAndDatastreamId(deviceId,
                                datastreamId);
                        System.out.println("deleted");
                        System.out.println(deleted);
                        datastream.setDeviceId(deviceId);
                        existingDatastreams.set(i, datastream);
                        deviceMetadataRepo.save(metadata);
                        return datastream;
                    }
                }
            }

            throw new IllegalAccessException("Invalid id");

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
            if (access.equals("Editor") || access.equals("Owner")) {
                device.getUserAccess().put(userId, accessLevel);
                deviceRepo.save(device);

                Dashboard dashboard = dashboardRepo.findById(device.getDashboardId()).get();
                dashboard.getUserAccess().put(userId, accessLevel);
                dashboardRepo.save(dashboard);

                User user = userRepo.findById(userId).get();
                user.getSharedDevices().add(deviceId);
                userRepo.save(user);
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
            device.getUserAccess().remove(userId);
            deviceRepo.save(device);

            Dashboard dashboard = dashboardRepo.findById(device.getDashboardId()).get();
            dashboard.getUserAccess().remove(userId);
            dashboardRepo.save(dashboard);

            User user = userRepo.findById(userId).get();
            user.getSharedDevices().remove(deviceId);
            userRepo.save(user);
            
            return "ok";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public List<Device> getSharedDevices() {
        User user = userService.whoAmI();
        List<Device> devices = deviceRepo.findAllById(user.getSharedDevices());
        devices.stream().forEach(device -> device.setMyAccess(getAccessLevel(device)));
        return devices;
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

    public ResponseEntity<byte[]> resolveImage(String id) {
        Image image = imageRepo.findById(id).get();
        byte[] imageFile = image.getContent();
        return ResponseEntity.ok().contentType(MediaType.valueOf(image.getImageType())).body(imageFile);
    }

    public String saveOta(String deviceId, MultipartFile file, String version) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            String filePath = System.getProperty("user.dir") + "/Uploads" + File.separator + deviceId + ".bin";
            String fileUploadStatus;

            try {
                FileOutputStream fout = new FileOutputStream(filePath);
                fout.write(file.getBytes());
                fout.close();
                fileUploadStatus = "File Uploaded Successfully";
                device.setTargetVersion(version);
                deviceRepo.save(device);
            } catch (Exception e) {
                e.printStackTrace();
                fileUploadStatus = "Error in uploading file: " + e;
            }
            return fileUploadStatus;
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    // TODO: verify version with template
    public String saveDeviceConstants(String deviceId, String version, String data) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceConstants constants = deviceConstantsRepo.findByDeviceIdAndVersion(deviceId, version);
            if (constants == null) {
                constants = new DeviceConstants();
            }
            constants.setDeviceId(deviceId);
            constants.setVersion(version);
            constants.setData(data);

            deviceConstantsRepo.save(constants);
            return "ok";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    private String getAccessLevel(Device device) {
        String userId = userService.getMyUserId();

        if (device.getOwner().equals(userId)) {
            return "Owner";
        }
        return device.getUserAccess().getOrDefault(userId, "No Access");
    }
}

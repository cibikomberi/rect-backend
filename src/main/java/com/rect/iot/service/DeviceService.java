package com.rect.iot.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.Image;
import com.rect.iot.model.automation.Automation;
import com.rect.iot.model.automation.ScheduleAutomation;
import com.rect.iot.model.dashboard.Dashboard;
import com.rect.iot.model.dashboard.DashboardData;
import com.rect.iot.model.dashboard.Widget;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceConstants;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.node.Flow;
import com.rect.iot.model.template.Template;
import com.rect.iot.model.template.VersionControl;
import com.rect.iot.model.thing.ThingData;
import com.rect.iot.model.user.User;
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
import com.rect.iot.repository.VersionControlRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepo deviceRepo;
    private final TemplateRepo templateRepo;
    private final TemplateService templateService;
    private final DeviceMetadataRepo deviceMetadataRepo;
    private final DashboardDataRepo dashboardDataRepo;
    private final DashboardRepo dashboardRepo;
    private final FlowRepo flowRepo;
    private final UserService userService;
    private final ThingDataRepo thingDataRepo;
    private final UserRepo userRepo;
    private final ImageRepo imageRepo;
    private final DeviceConstantsRepo deviceConstantsRepo;
    private final BuildService buildService;
    private final VersionControlRepo versionControlRepo;
    private final MqttMessageSender mqttMessageSender;    
    private final DynamicTaskScheduler taskScheduler;

    public List<Device> getMyDevices() {
        String userId = userService.getMyUserId();
        return deviceRepo.findByOwner(userId);
    }

    public Device createDevice(String name, String board, String templateId) throws IllegalAccessException {
        String userId = userService.getMyUserId();

        Template template = templateRepo.findById(templateId).get();
        String access = templateService.getAccessLevel(template);
        if (!(access.equals("Editor") || access.equals("Owner"))) {
            throw new IllegalAccessException("User does not have access to this device");
        }
        DeviceMetadata deviceMetadata = deviceMetadataRepo.save(DeviceMetadata.builder()
                .automations(new ArrayList<>())
                .datastreams(new ArrayList<>())
                .build());

                DashboardData data = DashboardData.builder()
                .days("1")
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();
        DashboardData mobileData = DashboardData.builder()
                .days("1")
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();
        DashboardData tabletData = DashboardData.builder()
                .days("1")
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();
        DashboardData largeData = DashboardData.builder()
                .days("1")
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();

        DashboardData dashboardData = dashboardDataRepo.save(data);
        DashboardData mobileDashboardData = dashboardDataRepo.save(mobileData);
        DashboardData tabletDashboardData = dashboardDataRepo.save(tabletData);
        DashboardData largeDashboardData = dashboardDataRepo.save(largeData);

        Dashboard savedDashboard = dashboardRepo.save(Dashboard.builder()
                .name(name)
                .access("Private")
                .dashboardDataId(dashboardData.getId())
                .mobileDashboardDataId(mobileDashboardData.getId())
                .tabletDashboardDataId(tabletDashboardData.getId())
                .largeDashboardDataId(largeDashboardData.getId())
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
                .apiKey(generateApiKey())
                .inheritTemplate(true)
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
            device.setMyAccess(access);
            return device;
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

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

            if (newInfo.getInheritTemplate() && device.getInheritTemplate().equals(false)) {
                Template template = templateRepo.findById(device.getTemplateId()).get();
                VersionControl versionControl = versionControlRepo.findByTemplateIdAndVersion(device.getTemplateId(),
                        template.getProductionVersion());
                buildService.buildProject(device.getTemplateId(), device, template.getProductionVersion(),
                        versionControl.getEnviroinment(), true);

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

    public String addAutomation(String deviceId, Automation automation) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            List<Automation> existingAutomations = metadata.getAutomations();
            for (Automation existingAutomation : existingAutomations) {
                if (existingAutomation.getName().equals(automation.getName())) {
                    throw new IllegalAccessException("Invalid name");
                }
            }
            if (automation instanceof ScheduleAutomation) {
                ScheduleAutomation scheduleAutomation = (ScheduleAutomation) automation;
                scheduleAutomation.setTaskId(taskScheduler.scheduleEvent(deviceId + automation.getName(), () -> scheduledAutomationHandler(deviceId, scheduleAutomation.getDatastream().getIdentifier(), convertStringToFloat(scheduleAutomation.getValue())), scheduleAutomation.getTime()));
            }
            existingAutomations.add(automation);
            deviceMetadataRepo.save(metadata);
            return "ok"; 
        }
        throw new IllegalAccessException("User does not have access to this device");
    }
    public String updateAutomation(String deviceId, Automation automation) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            List<Automation> existingAutomations = metadata.getAutomations();

            for (int i = 0; i < existingAutomations.size(); i++) {
                if (existingAutomations.get(i).getName().equals(automation.getName())) {
                    if (existingAutomations.get(i) instanceof ScheduleAutomation) {
                        ScheduleAutomation existingScheduleAutomation = (ScheduleAutomation) existingAutomations.get(i);
                        System.out.println(existingScheduleAutomation);
                        taskScheduler.cancelEvent(existingScheduleAutomation.getTaskId());
                    }
                    existingAutomations.remove(i);
                }
            }
            if (automation instanceof ScheduleAutomation) {
                ScheduleAutomation scheduleAutomation = (ScheduleAutomation) automation;
                scheduleAutomation.setTaskId(taskScheduler.scheduleEvent(deviceId + automation.getName(), () -> scheduledAutomationHandler(deviceId, scheduleAutomation.getDatastream().getIdentifier(), convertStringToFloat(scheduleAutomation.getValue())), scheduleAutomation.getTime()));
                System.out.println(scheduleAutomation.getTaskId());
            }
            existingAutomations.add(automation);
            deviceMetadataRepo.save(metadata);
            return "ok"; 
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String deleteAutomation(String deviceId, String automationName) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            List<Automation> existingAutomations = metadata.getAutomations();

            for (int i = 0; i < existingAutomations.size(); i++) {
                if (existingAutomations.get(i).getName().equals(automationName)) {
                    if (existingAutomations.get(i) instanceof ScheduleAutomation) {
                        ScheduleAutomation existingScheduleAutomation = (ScheduleAutomation) existingAutomations.get(i);
                        taskScheduler.cancelEvent(existingScheduleAutomation.getTaskId());
                    }
                    existingAutomations.remove(i);
                }
            }
            deviceMetadataRepo.save(metadata);
            return "ok";
        }
        throw new IllegalAccessException("User does not have access to this device");

    }

    private void scheduledAutomationHandler(String deviceId, String datastreamId, Object value) {
        System.out.println("Sche");
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("id", datastreamId);
        payload.put("data", value);
        mqttMessageSender.sendMessage("rect/device/" + deviceId + "/data", payload, false);
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
            String filePath = System.getProperty("user.dir") + "/uploads" + File.separator + deviceId + ".bin";
            String fileUploadStatus;

            try {
                FileOutputStream fout = new FileOutputStream(filePath);
                fout.write(file.getBytes());
                fout.close();
                fileUploadStatus = "File Uploaded Successfully";
                device.setTargetVersion(version);
                mqttMessageSender.sendMessage("rect/" + deviceId + "/ota", version, true);
                deviceRepo.save(device);
            } catch (Exception e) {
                e.printStackTrace();
                fileUploadStatus = "Error in uploading file: " + e;
            }
            return fileUploadStatus;
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public List<String> getAvailableVersions(String deviceId) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            return versionControlRepo.findByTemplateIdOrderByCreateDateDesc(device.getTemplateId()).stream()
                    .map(versionControl -> versionControl.getVersion()).toList();
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String saveDeviceConstants(String deviceId, String version, String data) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            List<VersionControl> versionControls = versionControlRepo.findByTemplateId(device.getTemplateId());
            boolean isVersionValid = false;
            for (VersionControl versionControl : versionControls) {
                if (versionControl.getVersion().equals(version)) {
                    isVersionValid = true;
                    break;
                }
            }
            if (!isVersionValid) {
                throw new IllegalAccessException("Invalid version");
            }

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

    public String getDeviceConstants(String deviceId, String version) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            DeviceConstants constants = deviceConstantsRepo.findByDeviceIdAndVersion(deviceId, version);
            if (constants == null) {
                return "";
            }
            return constants.getData();
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public Map<String, String> getDeviceConstantsVS(String deviceId) throws IllegalAccessException {
        Device device = deviceRepo.findById(deviceId).get();
        String access = getAccessLevel(device);

        if (access.equals("Editor") || access.equals("Owner")) {
            String devVersion = templateRepo.findById(device.getTemplateId()).get().getDevVersion();
            DeviceConstants constants = deviceConstantsRepo.findByDeviceIdAndVersion(deviceId, devVersion);

            Map<String, String> res = new HashMap<>();
            res.put("API_KEY", device.getApiKey());
            res.put("DEVICE_ID", deviceId);
            if (constants != null) {
                res.put("DATA", constants.getData());
            }
            return res;
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public static Object convertStringToFloat(String input) {
        try {
            return Float.parseFloat(input); // Try to convert to float
        } catch (NumberFormatException e) {
            return input; // If conversion fails, return the original string
        }
    }

    String generateApiKey() {
        char[] alphabets = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z', '0', '1',
                '2', '3', '4', '5', '6', '7', '8', '9' };

        String key = "";
        for (int i = 0; i < 15; i++)
            key = key + alphabets[(int) (Math.random() * 100 % alphabets.length)];

        return key;
    }

    private String getAccessLevel(Device device) {
        String userId = userService.getMyUserId();

        if (device.getOwner().equals(userId)) {
            return "Owner";
        }
        return device.getUserAccess().getOrDefault(userId, "No Access");
    }
}

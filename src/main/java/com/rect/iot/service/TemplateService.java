package com.rect.iot.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import javax.naming.directory.InvalidAttributesException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.model.BuildJob;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.Image;
import com.rect.iot.model.device.BuildErrors;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.node.Flow;
import com.rect.iot.model.template.Template;
import com.rect.iot.model.template.TemplateMetadata;
import com.rect.iot.model.template.VersionControl;
import com.rect.iot.model.user.User;
import com.rect.iot.repository.BuildErrorRepo;
import com.rect.iot.repository.BuildJobRepo;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.FlowRepo;
import com.rect.iot.repository.ImageRepo;
import com.rect.iot.repository.TemplateMetadataRepo;
import com.rect.iot.repository.TemplateRepo;
import com.rect.iot.repository.UserRepo;
import com.rect.iot.repository.VersionControlRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepo templateRepo;
    private final DeviceRepo deviceRepo;
    private final DeviceMetadataRepo deviceMetadataRepo;
    private final TemplateMetadataRepo templateMetadataRepo;
    private final FlowRepo flowRepo;
    private final UserService userService;
    private final UserRepo userRepo;
    private final ImageRepo imageRepo;
    private final VersionControlRepo versionControlRepo;
    private final BuildService buildService;
    private final BuildErrorRepo buildErrorRepo;
    private final BuildJobRepo buildJobRepo;
    private final MqttMessageSender mqttMessageSender;

    public List<Template> getMyTemplates() {
        String userId = userService.getMyUserId();
        return templateRepo.findByOwner(userId);
    }

    public Template createTemplate(String name, String board) {
        String userId = userService.getMyUserId();

        TemplateMetadata metadata = templateMetadataRepo.save(TemplateMetadata.builder()
                .datastreams(new ArrayList<>())
                .build());
        return templateRepo.save(Template.builder()
                .board(board)
                .name(name)
                .productionVersion("")
                .metadataId(metadata.getId())
                .owner(userId)
                .userAccess(new HashMap<>())
                .build());
    }

    public Template getTemplateInfo(String id) throws IllegalAccessException {
        Template template = templateRepo.findById(id).get();
        String access = getAccessLevel(template);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            template.setMyAccess(access);
            return template;
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public Template updateTemplateInfo(String id, Template newInfo, MultipartFile multipartImage)
            throws IllegalAccessException, IOException {
        Template template = templateRepo.findById(id).get();
        String access = getAccessLevel(template);

        if (access.equals("Editor") || access.equals("Owner")) {
            if (multipartImage != null) {
                Image image = new Image();
                image.setImageType(multipartImage.getContentType());
                image.setContent(multipartImage.getBytes());
                if (template.getImage() != null) {
                    imageRepo.deleteById(template.getImage());
                }
                Image savedImage = imageRepo.save(image);
                template.setImage(savedImage.getId());
            }
            template.setName(newInfo.getName());
            template.setDescription(newInfo.getDescription());
            return templateRepo.save(template);
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public TemplateMetadata getTemplateMetadata(String id) throws IllegalAccessException {
        Template template = templateRepo.findById(id).get();
        String access = getAccessLevel(template);

        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            TemplateMetadata metadata = templateMetadataRepo.findById(template.getMetadataId()).get();
            metadata.setUserAccess(template.getUserAccess());
            return metadata;
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public Datastream addDatastream(String templateId, Datastream datastream) throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);

        if (access.equals("Editor") || access.equals("Owner")) {
            TemplateMetadata metadata = templateMetadataRepo.findById(template.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();
            if (!existingDatastreams.contains(datastream)) {
                existingDatastreams.add(datastream);
                var saved = templateMetadataRepo.save(metadata);
                updateDeviceDatastreams(saved.getDatastreams(), templateId);
                return datastream;
            }
            throw new IllegalAccessException("Id already exists");
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public Datastream updateDatastream(String templateId, String datastreamId, Datastream datastream)
            throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);

        if (access.equals("Editor") || access.equals("Owner")) {
            TemplateMetadata metadata = templateMetadataRepo.findById(template.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();

            if (!datastreamId.equals(datastream.getIdentifier()) && existingDatastreams.contains(datastream)) {
                throw new IllegalAccessException("Id already exists");
            }

            for (int i = 0; i < existingDatastreams.size(); i++) {
                if (existingDatastreams.get(i).getIdentifier().equals(datastreamId)) {

                    if (existingDatastreams.get(i).getIdentifier().equals(datastream.getIdentifier())
                            && existingDatastreams.get(i).getType().equals(datastream.getType())) {
                        existingDatastreams.set(i, datastream);
                        templateMetadataRepo.save(metadata);
                        return datastream;
                    } else {
                        existingDatastreams.set(i, datastream);
                        var saved = templateMetadataRepo.save(metadata);
                        updateDeviceDatastreams(saved.getDatastreams(), templateId);
                        return datastream;
                    }
                }
            }
            throw new IllegalAccessException("Invalid id");

        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String deleteDatastream(String templateId, String datastreamId) throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);

        if (access.equals("Editor") || access.equals("Owner")) {
            TemplateMetadata metadata = templateMetadataRepo.findById(template.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();
            for (int i = 0; i < existingDatastreams.size(); i++) {
                if (existingDatastreams.get(i).getIdentifier().equals(datastreamId)) {
                    existingDatastreams.remove(i);
                    var saved = templateMetadataRepo.save(metadata);
                    updateDeviceDatastreams(saved.getDatastreams(), templateId);
                    return "ok";
                }
            }
            return "Invalid id";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    private void updateDeviceDatastreams(List<Datastream> datastreams, String templateId) {
        List<Device> devices = deviceRepo.findByTemplateIdAndInheritTemplate(templateId, true);
        deviceMetadataRepo.saveAll(deviceMetadataRepo.findAllById(
                devices.stream()
                        .map(device -> device.getMetadataId())
                        .toList())
                .stream()
                .map(metadata -> {
                    metadata.setDatastreams(datastreams);
                    return metadata;
                })
                .toList());
    }

    public String updateUserAccess(String templateId, String userId, String accessLevel) throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);

        if (accessLevel.equals("Editor") || accessLevel.equals("Viewer")) {
            if (access.equals("Editor") || access.equals("Owner")) {
                User user = userRepo.findById(userId).get();
                user.getSharedTemplates().add(templateId);
                userRepo.save(user);
                template.getUserAccess().put(userId, accessLevel);
                templateRepo.save(template);
                return "ok";
            }
            return "fail";
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public String removeUserAccess(String templateId, String userId) throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);

        if (access.equals("Editor") || access.equals("Owner")) {
            User user = userRepo.findById(userId).get();
            user.getSharedTemplates().remove(templateId);
            userRepo.save(user);
            template.getUserAccess().remove(userId);
            templateRepo.save(template);
            return "ok";
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public List<Template> getSharedTemplates() {
        User user = userService.whoAmI();
        List<Template> templates = templateRepo.findAllById(user.getSharedTemplates());
        templates.stream().forEach(template -> template.setMyAccess(getAccessLevel(template)));
        return templates;
    }

    public List<VersionControl> getTemplateVersions(String templateId) throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            return versionControlRepo.findByTemplateIdOrderByCreateDateDesc(templateId);
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public VersionControl createTemplateVersions(String templateId, String version, String description, String enviroinment)
            throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);
        if (access.equals("Editor") || access.equals("Owner")) {
            return versionControlRepo.save(VersionControl.builder()
                    .templateId(templateId)
                    .version(version)
                    .enviroinment(enviroinment)
                    .description(description)
                    .createDate(LocalDateTime.now())
                    .build());
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public String updateBuild(String templateId, String version, String type) throws InvalidAttributesException, IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        return updateBuild(template, version, type);
    }

    public String reBuild(String templateId) throws InvalidAttributesException, IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        return updateBuild(template, template.getBuildVersion(), "Build");
    }

    public String updateBuild(Template template, String version, String type)
            throws InvalidAttributesException, IllegalAccessException {
        String access = getAccessLevel(template);
        if (access.equals("Editor") || access.equals("Owner")) {
            if ("Build".equals(type)) {
                template.setBuildVersion(version);
                templateRepo.save(template);
                Optional<BuildJob> job = buildJobRepo.findById(template.getId());
                BuildJob buildJob;
                if (job.isPresent()) {
                    buildJob = job.get();
                    for (Entry<String, String> deviceEntry : buildJob.getDevices().entrySet()) {
                        if (deviceEntry.getValue().equals("Started")) {
                            throw new InvalidAttributesException("A build is already in progress");
                        }
                    }
                } else { 
                    buildJob = new BuildJob();
                }

                List<Device> devicesToUpdate = deviceRepo.findByTemplateIdAndInheritTemplate(template.getId(), true);

                buildJob.setId(template.getId());
                buildJob.setDevices(new HashMap<>());
                buildErrorRepo.deleteAllByTemplateId(template.getId());
                for (Device device : devicesToUpdate) {
                    buildJob.getDevices().put(device.getId(), "Started");
                }
                buildJobRepo.save(buildJob);

                VersionControl versionControl = versionControlRepo.findByTemplateIdAndVersion(template.getId(), version);
                for (Device device : devicesToUpdate) {
                    buildService.buildProject(template.getId(), device, version, versionControl.getEnviroinment(), false);
                }
                return "ok";
            }
            if ("Dev".equals(type)) {
                template.setDevVersion(version);
                templateRepo.save(template);
                return "ok";
            }
            throw new InvalidAttributesException("Invalid type");
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public BuildJob getBuildStatus(String templateId) throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);
        if (access.equals("Editor") || access.equals("Owner")) {
            Optional<BuildJob> buildJob = buildJobRepo.findById(templateId);
            if (buildJob.isPresent()) {
                return buildJob.get();
            }
            return null;
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public List<BuildErrors> getBuildErrors(String templateId) throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);
        if (access.equals("Editor") || access.equals("Owner")) {
            List<BuildErrors> errors = buildErrorRepo.findByTemplateId(templateId);
            for (BuildErrors error : errors) {
                error.setDeviceName(deviceRepo.findById(error.getDeviceId()).get().getName());
            }
            return errors;
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public String deployTemplate(String templateId) throws InvalidAttributesException, IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);
        if (access.equals("Editor") || access.equals("Owner")) {
            BuildJob buildJob = buildJobRepo.findById(templateId).get();
            for (Entry<String, String> deviceEntry : buildJob.getDevices().entrySet()) {
                if (!deviceEntry.getValue().equals("Success")) {
                    throw new InvalidAttributesException("Build not completed");
                }
            }
            for (Entry<String, String> deviceEntry : buildJob.getDevices().entrySet()) {
                mqttMessageSender.sendMessage("rect/" + deviceEntry.getKey() + "/ota", template.getBuildVersion(), true);
            }

            template.setProductionVersion(template.getBuildVersion());
            templateRepo.save(template);
            return "ok";
        }
        throw new IllegalAccessException("User does not have access to this template");
    }

    public ResponseEntity<byte[]> resolveImage(String id) {
        Image image = imageRepo.findById(id).get();
        byte[] imageFile = image.getContent();
        return ResponseEntity.ok().contentType(MediaType.valueOf(image.getImageType())).body(imageFile);
    }

    public Flow saveTemplateFlow(String templateId, Flow flow) {

        Template template = templateRepo.findById(templateId).get();

        // Set the existing id
        flow.setId(template.getFlowId());
        Flow savedFlow = flowRepo.save(flow);

        // Save the flow
        template.setFlowId(savedFlow.getId());
        templateRepo.save(template);

        return flow;
    }

    public Flow getFlow(String id) {
        Template template = templateRepo.findById(id).get();

        if (template.getFlowId() == null) {

            return Flow.builder().edges(new ArrayList<>()).nodes(new ArrayList<>()).build();
            // return null;
        }
        Flow flow = flowRepo.findById(template.getFlowId()).get();
        return flow;
    }

    public String getAccessLevel(Template template) {
        String userId = userService.getMyUserId();

        if (template.getOwner().equals(userId)) {
            return "Owner";
        }
        return template.getUserAccess().getOrDefault(userId, "No Access");
    }

}

package com.rect.iot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.Image;
import com.rect.iot.model.Template;
import com.rect.iot.model.User;
import com.rect.iot.model.node.Flow;
import com.rect.iot.model.template.TemplateMetadata;
import com.rect.iot.repository.FlowRepo;
import com.rect.iot.repository.ImageRepo;
import com.rect.iot.repository.TemplateMetadataRepo;
import com.rect.iot.repository.TemplateRepo;
import com.rect.iot.repository.UserRepo;



@Service
public class TemplateService {

    @Autowired
    private TemplateRepo templateRepo;
    @Autowired
    private TemplateMetadataRepo templateMetadataRepo;
    @Autowired
    private FlowRepo flowRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ImageRepo imageRepo;


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
                .metadataId(metadata.getId())
                .owner(userId)
                .userAccess(new HashMap<>())
                .build());
    }

    public Template getTemplateInfo(String id) throws IllegalAccessException {
        Template template = templateRepo.findById(id).get();
        String access = getAccessLevel(template);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            return template;
        }
        throw new IllegalAccessException("User does not have access to this template");
    }
    
    //TODO: change in react app
    //TODO: add method to update access and datastream
    public Template updateTemplateInfo(String id, Template newInfo, MultipartFile multipartImage) throws IllegalAccessException, IOException {
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
    
    public TemplateMetadata getTemplateMetadata(String id) throws IllegalAccessException{
        Template template = templateRepo.findById(id).get();
        String access = getAccessLevel(template);
        
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            TemplateMetadata metadata = templateMetadataRepo.findById(template.getMetadataId()).get();
            metadata.setUserAccess(template.getUserAccess());
            return metadata;
        }
        throw new IllegalAccessException("User does not have access to this template");
    }
                        // TODO: need to update the device too

    public Datastream addDatastream(String templateId, Datastream datastream) throws IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = getAccessLevel(template);

        if (access.equals("Editor") || access.equals("Owner")) {
            TemplateMetadata metadata = templateMetadataRepo.findById(template.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();
            if (!existingDatastreams.contains(datastream)) {
                existingDatastreams.add(datastream);
                templateMetadataRepo.save(metadata);
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
                        // TODO: need to update the device too
                        // List<ThingData<?>> deleted = thingDataRepo.deleteByDeviceIdAndDatastreamId(deviceId,
                        //         datastreamId);
                        // System.out.println("deleted");
                        // System.out.println(deleted);
                        // datastream.setDeviceId(deviceId);
                        existingDatastreams.set(i, datastream);
                        templateMetadataRepo.save(metadata);
                        return datastream;
                    }
                }
            }
            throw new IllegalAccessException("Invalid id");

        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    //TODO need to update devices
    public String deleteDatastream(String deviceId, String datastreamId) throws IllegalAccessException {
        Template template = templateRepo.findById(deviceId).get();
        String access = getAccessLevel(template);

        if (access.equals("Editor") || access.equals("Owner")) {
            TemplateMetadata metadata = templateMetadataRepo.findById(template.getMetadataId()).get();
            List<Datastream> existingDatastreams = metadata.getDatastreams();
            for (int i = 0; i < existingDatastreams.size(); i++) {
                if (existingDatastreams.get(i).getIdentifier().equals(datastreamId)) {
                    existingDatastreams.remove(i);
                    templateMetadataRepo.save(metadata);
                    return "ok";
                }
            }
            return "Invalid id";
        }
        throw new IllegalAccessException("User does not have access to this device");
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
        throw new IllegalAccessException("User does not have access to this device");
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
        System.out.println(template);

        if (template.getFlowId() == null) {
        System.out.println("flow");

            return Flow.builder().edges(new ArrayList<>()).nodes(new ArrayList<>()).build();
            // return null;
        }
        System.out.println(flowRepo.findAll());
        Flow flow = flowRepo.findById(template.getFlowId()).get();
        System.out.println(flow);
        return flow;
    }

    private String getAccessLevel(Template template) {
        String userId = userService.getMyUserId();

        if (template.getOwner().equals(userId)) {
            return "Owner";
        }
        return template.getUserAccess().getOrDefault(userId, "No Access");
    }

}

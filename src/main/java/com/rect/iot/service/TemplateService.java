package com.rect.iot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Template;
import com.rect.iot.model.node.Flow;
import com.rect.iot.model.template.TemplateMetadata;
import com.rect.iot.repository.FlowRepo;
import com.rect.iot.repository.TemplateMetadataRepo;
import com.rect.iot.repository.TemplateRepo;



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
    public Template updateTemplateInfo(String id, Template newInfo) throws IllegalAccessException {
        Template template = templateRepo.findById(id).get();
        String access = getAccessLevel(template);

        if (access.equals("Editor") || access.equals("Owner")) {
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
            return templateMetadataRepo.findById(template.getMetadataId()).get();
        }
        throw new IllegalAccessException("User does not have access to this template");
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

package com.rect.iot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;

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

    public Template getTemplateInfo(Long templateId) {
        return templateRepo.findById(templateId).get();
    }
    
    public List<Template> getTemplates(Long id) {
        return templateRepo.findAll();
    }

    public Template updateTemplateInfo(@PathVariable Long id, @RequestPart Template newInfo, @RequestPart TemplateMetadata newMetadata) {
        Template template = templateRepo.findById(id).get();
        TemplateMetadata metadata = templateMetadataRepo.findById(template.getMetadataId()).get();

        template.setName(newInfo.getName());
        template.setDescription(newInfo.getDescription());

        metadata.setDatastreams(newMetadata.getDatastreams());
        metadata.setAccessControls(newMetadata.getAccessControls());

        templateMetadataRepo.save(metadata);

        return templateRepo.save(template);
    }

    public TemplateMetadata getTemplateMetadata(Long id){
        return templateMetadataRepo.findById(templateRepo
                    .findById(id)
                    .get()
                    .getMetadataId())
                .get();
    }

    public Template createTemplate(String name, String board) {
        TemplateMetadata metadata = templateMetadataRepo.save(TemplateMetadata.builder()
                .datastreams(new ArrayList<>())
                .accessControls(new ArrayList<>())
                .build());
        return templateRepo.save(Template.builder()
                .board(board)
                .name(name)
                .metadataId(metadata.getId())
                .build());
    }

    public Flow saveTemplateFlow(Long templateId, Flow flow) {

        Template template = templateRepo.findById(templateId).get();

        // Set the existing id
        flow.setId(template.getFlowId());
        Flow savedFlow = flowRepo.save(flow);

        // Save the flow
        template.setFlowId(savedFlow.getId());
        templateRepo.save(template);

        return flow;
    }

    public Flow getFlow(Long id) {
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
    };

}

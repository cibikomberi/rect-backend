package com.rect.iot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Flow;
import com.rect.iot.model.Template;
import com.rect.iot.repository.FlowRepo;
import com.rect.iot.repository.TemplateRepo;



@Service
public class TemplateService {

    @Autowired
    private TemplateRepo templateRepo;
    @Autowired
    private FlowRepo flowRepo;

    public List<Template> getTemplates(Long id) {
        return templateRepo.findAll();
    }

    public Template createTemplate(Long id, String name, String board) {
        return templateRepo.save(Template.builder()
                .board(board)
                .name(name)
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

package com.rect.iot.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rect.iot.model.Template;
import com.rect.iot.model.node.Flow;
import com.rect.iot.model.template.TemplateMetadata;
import com.rect.iot.service.TemplateService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;



@RestController
@CrossOrigin
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/templates/{id}")
    public List<Template> getTemplates(@PathVariable Long id) {
        return templateService.getTemplates(id);
    }
    
    @PostMapping("/template")
    public Template createTemplate( @RequestBody ObjectNode json) {
        return templateService.createTemplate(json.get("name").asText(), json.get("board").asText());
    }

    @GetMapping("/template/{templateId}")
    public Template getTemplateInfo(@PathVariable Long templateId) {
        return templateService.getTemplateInfo(templateId);
    }

    @PutMapping("template/{id}")
    public Template updateTemplateInfo(@PathVariable Long id, @RequestPart Template info, @RequestPart TemplateMetadata metadata) {
        System.out.println(id);
        System.out.println(info);
        System.out.println(metadata);
        return templateService.updateTemplateInfo(id, info, metadata);
    }

    @GetMapping("/template/metadata/{templateId}")
    public TemplateMetadata getTemplateMetadata(@PathVariable Long templateId){
        return templateService.getTemplateMetadata(templateId);
    }

    @PostMapping("/template/flow/{id}")
    public ResponseEntity<?> saveFlow(@PathVariable Long id, @RequestBody Flow flow) {
        return new ResponseEntity<>(templateService.saveTemplateFlow(id, flow), HttpStatus.OK);
    }

    @GetMapping("/template/flow/{id}")
    public ResponseEntity<?> getFlow(@PathVariable Long id) {
        return new ResponseEntity<>(templateService.getFlow(id), HttpStatus.OK);
    }
}

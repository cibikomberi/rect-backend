package com.rect.iot.controller;

import java.io.IOException;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rect.iot.model.BuildJob;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.device.BuildErrors;
import com.rect.iot.model.node.Flow;
import com.rect.iot.model.template.Template;
import com.rect.iot.model.template.TemplateMetadata;
import com.rect.iot.model.template.VersionControl;
import com.rect.iot.service.TemplateService;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;
    
    @GetMapping("/templates")
    public List<Template> getMyTemplates() {
        return templateService.getMyTemplates();
    }
    
    @PostMapping("/template")
    public Template createTemplate( @RequestBody ObjectNode json) {
        return templateService.createTemplate(json.get("name").asText(), json.get("board").asText());
    }

    @GetMapping("/template/{id}")
    public Template getTemplateInfo(@PathVariable String id) throws IllegalAccessException {
        return templateService.getTemplateInfo(id);
    }

    @PutMapping("template/{id}")
    public Template updateTemplateInfo(@PathVariable String id, @RequestPart Template info, @RequestPart(required = false) MultipartFile image) throws IllegalAccessException, IOException {
        return templateService.updateTemplateInfo(id, info, image);
    }

    @GetMapping("/template/metadata/{templateId}")
    public TemplateMetadata getTemplateMetadata(@PathVariable String templateId) throws IllegalAccessException{
        return templateService.getTemplateMetadata(templateId);
    }

    @PostMapping("/template/datastream/{templateId}")
    public Datastream addDatastream(@PathVariable String templateId, @RequestBody Datastream datastream) throws IllegalAccessException {
        return templateService.addDatastream(templateId, datastream);
    }
    
    @PutMapping("/template/datastream/{templateId}/{datastreamId}")
    public Datastream updateDatastream(@PathVariable String templateId, @PathVariable String datastreamId, @RequestBody Datastream datastream) throws IllegalAccessException {
        return templateService.updateDatastream(templateId, datastreamId, datastream);
    }

    @DeleteMapping("/template/datastream/{templateId}/{datastreamId}")
    public String deleteDatastream(@PathVariable String templateId, @PathVariable String datastreamId) throws IllegalAccessException {
        return templateService.deleteDatastream(templateId, datastreamId);
    }

    @PostMapping("/template/userAccess/{templateId}")
    public String updateUserAccess(@PathVariable String templateId, @RequestBody JsonNode json) throws IllegalAccessException{
        return templateService.updateUserAccess(templateId, json.get("user").asText(), json.get("access").asText());
    }

    @DeleteMapping("/template/userAccess/{templateId}/{userId}")
    public String removeUserAccess(@PathVariable String templateId, @PathVariable String userId) throws IllegalAccessException {
        return templateService.removeUserAccess(templateId, userId);
    }

    @GetMapping("/templates/shared")
    public List<Template> getSharedTemplates() {
        return templateService.getSharedTemplates();
    }

    @GetMapping("/template/version-control/{templateId}")
    public List<VersionControl> getTemplateVersions(@PathVariable String templateId) throws IllegalAccessException {
        return templateService.getTemplateVersions(templateId);
    }

    @PostMapping("/template/version-control/{templateId}")
    public VersionControl createTemplateVersions(@PathVariable String templateId, @RequestBody JsonNode json) throws IllegalAccessException {
        return templateService.createTemplateVersions(templateId, json.get("version").asText(), json.get("description").asText(), json.get("enviroinment").asText());
    }

    @PostMapping("/template/build/{templateId}")
    public String updateBuild(@PathVariable String templateId, @RequestBody JsonNode json) throws InvalidAttributesException, IllegalAccessException, IOException, InterruptedException {
        return templateService.updateBuild(templateId, json.get("version").asText(), json.get("type").asText());
    }
    @PostMapping("/template/rebuild/{templateId}")
    public String reBuild(@PathVariable String templateId) throws InvalidAttributesException, IllegalAccessException, IOException, InterruptedException {
        return templateService.reBuild(templateId);
    }

    @GetMapping("/template/build/status/{templateId}")
    public BuildJob getBuildStatus(@PathVariable String templateId) throws IllegalAccessException {
        return templateService.getBuildStatus(templateId);
    }
    
    @GetMapping("/template/build/errors/{templateId}")
    public List<BuildErrors> getBuildErrors(@PathVariable String templateId) throws IllegalAccessException {
        return templateService.getBuildErrors(templateId);
    }

    @PostMapping("/template/deploy/{templateId}")
    public String deployTemplate(@PathVariable String templateId) throws InvalidAttributesException, IllegalAccessException {
        return templateService.deployTemplate(templateId);
    }
    
    @GetMapping("/template/image/{id}")
    public ResponseEntity<byte[]> resolveImage(@PathVariable String id){
        return templateService.resolveImage(id);
    }

    @PostMapping("/template/flow/{id}")
    public ResponseEntity<?> saveFlow(@PathVariable String id, @RequestBody Flow flow) {
        return new ResponseEntity<>(templateService.saveTemplateFlow(id, flow), HttpStatus.OK);
    }

    @GetMapping("/template/flow/{id}")
    public ResponseEntity<?> getFlow(@PathVariable String id) {
        return new ResponseEntity<>(templateService.getFlow(id), HttpStatus.OK);
    }
}

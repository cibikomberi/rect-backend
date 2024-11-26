package com.rect.iot.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rect.iot.model.Flow;
import com.rect.iot.model.Template;
import com.rect.iot.service.TemplateService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@CrossOrigin
@RequestMapping("/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/{id}/list")
    public List<Template> getTemplates(@PathVariable Long id) {
        return templateService.getTemplates(id);
    }

    @PostMapping("/{id}/new")
    public Template createTemplate(@PathVariable Long id, @RequestBody ObjectNode json) {
        return templateService.createTemplate(id, json.get("name").asText(), json.get("board").asText());
    }

    @PostMapping("/{id}/flow")
    public ResponseEntity<?> saveFlow(@PathVariable Long id, @RequestBody Flow flow) {
        return new ResponseEntity<>(templateService.saveTemplateFlow(id, flow), HttpStatus.OK);
    }

    @GetMapping("/{id}/flow")
    public ResponseEntity<?> getFlow(@PathVariable Long id) {
        return new ResponseEntity<>(templateService.getFlow(id), HttpStatus.OK);
    }
}

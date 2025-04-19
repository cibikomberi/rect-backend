package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.template.Template;

import java.util.List;

@Repository
public interface TemplateRepo extends MongoRepository<Template, String> {
    List<Template> findByOwner(String owner);
}
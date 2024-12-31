package com.rect.iot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.Template;

@Repository
public interface TemplateRepo extends MongoRepository<Template, String> {
    List<Template> findByOwner(String owner);
}
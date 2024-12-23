package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.template.TemplateMetadata;

@Repository
public interface TemplateMetadataRepo extends MongoRepository<TemplateMetadata, String> {
    
}

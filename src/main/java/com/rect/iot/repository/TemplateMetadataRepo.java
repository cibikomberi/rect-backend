package com.rect.iot.repository;

import com.rect.iot.model.template.TemplateMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateMetadataRepo extends MongoRepository<TemplateMetadata, String> {
    
}

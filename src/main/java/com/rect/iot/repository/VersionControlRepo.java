package com.rect.iot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.VersionControl;

@Repository
public interface VersionControlRepo extends MongoRepository<VersionControl, String> {
    List<VersionControl> findByTemplateIdOrderByCreateDateDesc(String templateId);
    List<VersionControl> findByTemplateId(String templateId);
    VersionControl findByTemplateIdAndVersion(String templateId, String version);
}

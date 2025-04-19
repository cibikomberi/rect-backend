package com.rect.iot.repository;

import com.rect.iot.model.device.BuildErrors;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BuildErrorRepo extends MongoRepository<BuildErrors, String>{
    List<BuildErrors> findByTemplateId(String templateId);
    List<BuildErrors> deleteAllByTemplateId(String templateId);
}

package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.BuildErrors;
import java.util.List;


@Repository
public interface BuildErrorRepo extends MongoRepository<BuildErrors, String>{
    List<BuildErrors> findByTemplateId(String templateId);
}

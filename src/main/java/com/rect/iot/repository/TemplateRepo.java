package com.rect.iot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.Template;

@Repository
public interface TemplateRepo extends JpaRepository<Template, Long> {
    
}
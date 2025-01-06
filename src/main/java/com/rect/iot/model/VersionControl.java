package com.rect.iot.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class VersionControl {
    @Id
    private String id;

    private String version;
    private String description;

    private String templateId;

    @CreatedDate
    private LocalDateTime createDate;
}

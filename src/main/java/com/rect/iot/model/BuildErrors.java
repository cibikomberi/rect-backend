package com.rect.iot.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
public class BuildErrors {
    @Id
    private String id;
    private String templateId;
    private String deviceId;
    private String errorData;

    @Transient
    private String deviceName;
}

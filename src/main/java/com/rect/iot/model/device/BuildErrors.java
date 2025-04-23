package com.rect.iot.model.device;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class BuildErrors {
    @Id
    private String id;
    @Indexed
    private String templateId;
    private String deviceId;
    private String errorData;

    @Transient
    private String deviceName;
}

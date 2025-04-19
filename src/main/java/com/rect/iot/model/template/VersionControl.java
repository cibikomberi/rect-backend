package com.rect.iot.model.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class VersionControl {
    @Id
    private String id;

    private String version;
    private String enviroinment;
    private String description;

    private String templateId;

    @CreatedDate
    private LocalDateTime createDate;
}

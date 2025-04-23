package com.rect.iot.model.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "template_date", def = "{'templateId' : 1, 'createDate': -1}"),
    @CompoundIndex(name = "template_version", def = "{'templateId' : 1, 'version': 1}")
})
public class VersionControl {
    @Id
    private String id;

    private String version;
    private String enviroinment;
    private String description;

    @Indexed
    private String templateId;

    @CreatedDate
    private LocalDateTime createDate;
}

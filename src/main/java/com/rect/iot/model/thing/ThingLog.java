package com.rect.iot.model.thing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ThingLog {
    private String id;

    private String deviceId;
    private LocalDateTime time;
    private String type;
    private String log;
}

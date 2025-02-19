package com.rect.iot.model.thing;

import java.time.LocalDateTime;

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
public class ThingLog {
    private String id;

    private String deviceId;
    private LocalDateTime time;
    private String type;
    private String log;
}

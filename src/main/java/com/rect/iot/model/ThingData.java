package com.rect.iot.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class  ThingData<T> {
    private String id;
    private String deviceId;
    private String datastreamId;
    private T value;
    private LocalDateTime dateTime;
}

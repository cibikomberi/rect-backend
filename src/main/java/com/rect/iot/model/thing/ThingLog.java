package com.rect.iot.model.thing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @CompoundIndex(def = "{'deviceId': 1, 'time': -1}")
})
public class ThingLog {
    private String id;

    @Indexed
    private String deviceId;
    private LocalDateTime time;
    private String type;
    private String log;
}

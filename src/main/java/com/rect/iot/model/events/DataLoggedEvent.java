package com.rect.iot.model.events;

import com.rect.iot.model.thing.ThingData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataLoggedEvent {
    private String type;
    private ThingData<?> payload;
}

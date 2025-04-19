package com.rect.iot.model.automation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rect.iot.model.Datastream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScheduleAutomation extends Automation {
    private String time;
    private Datastream datastream;
    private String value;

    @JsonIgnore
    private String taskId;
}

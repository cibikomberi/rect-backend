package com.rect.iot.model.automation;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.device.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StateAutomation extends Automation {
    private Datastream datastream;
    private Device targetDevice;
    private Datastream targetDatastream;
}

package com.rect.iot.model.automation;

import com.rect.iot.model.Datastream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailValueAutomation extends Automation {
    private Datastream datastream;
    private String[] emailList;
}

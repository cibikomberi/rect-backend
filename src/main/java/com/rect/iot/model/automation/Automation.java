package com.rect.iot.model.automation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,    // Use type property to distinguish subclasses
    include = JsonTypeInfo.As.PROPERTY, // Type information is included as a property
    property = "type"             // JSON field to indicate the type
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ScheduleAutomation.class, name = "schedule"),
    @JsonSubTypes.Type(value = StateAutomation.class, name = "state"),
    @JsonSubTypes.Type(value = EmailValueAutomation.class, name = "email-value"),
    @JsonSubTypes.Type(value = EmailStatusAutomation.class, name = "email-status"),
})
public abstract class Automation {
    String name;
}
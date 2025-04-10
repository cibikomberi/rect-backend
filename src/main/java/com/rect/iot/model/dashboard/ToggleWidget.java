package com.rect.iot.model.dashboard;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ToggleWidget extends Widget {
    String onLabel;
    String offLabel;
    Integer onVal;
    Integer offVal;
}

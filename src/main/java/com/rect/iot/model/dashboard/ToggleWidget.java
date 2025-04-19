package com.rect.iot.model.dashboard;


import lombok.*;

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

package com.rect.iot.model;

import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessControls {
    private Long userId;

    @Transient
    private String name;
    @Transient
    private String email;

    private String access;
}
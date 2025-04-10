package com.rect.iot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    String id;

    private String imageType;

    public byte[] content;
}

package com.rect.iot.model;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

    @Lob
    @Basic(fetch = FetchType.EAGER)
    public byte[] content;
}

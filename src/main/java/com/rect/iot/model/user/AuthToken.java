package com.rect.iot.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    private String id;

    @JsonIgnore
    @Indexed
    private String token;
    @Indexed
    @JsonIgnore
    private String userId;
    
    private LocalDateTime lastActiveTime;
    private String os;
    private String client;
    private String location;
}

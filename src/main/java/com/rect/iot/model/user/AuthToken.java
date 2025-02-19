package com.rect.iot.model.user;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    @JsonIgnore
    private String id;

    @JsonIgnore
    private String token;
    @JsonIgnore
    private String userId;
    
    private LocalDateTime lastActiveTime;
    private String os;
    private String client;
}

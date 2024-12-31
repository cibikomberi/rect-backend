package com.rect.iot.model;

import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;

    private String name;
    private String email;
    
    @JsonIgnore
    private String password;
    
    @JsonIgnore
    private Set<String> myTemplates;
    @JsonIgnore
    private Set<String> sharedTemplates;
    
    @JsonIgnore
    private Set<String> myDevices;
    @JsonIgnore
    private Set<String> sharedDevices;
    
    @JsonIgnore
    private Set<String> myDashboards;
    @JsonIgnore
    private Set<String> sharedDashboards;
}

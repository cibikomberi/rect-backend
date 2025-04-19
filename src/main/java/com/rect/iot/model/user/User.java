package com.rect.iot.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;

    private String name;
    private String role = "USER";
    private String email;
    private Long phone;

    private String imageId;
    
    @JsonIgnore
    private String password;
    

    @JsonIgnore
    private Set<String> sharedTemplates;
    
    @JsonIgnore
    private Set<String> sharedDevices;
    
    @JsonIgnore
    private Set<String> sharedDashboards;
}

package com.rect.iot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private Long id;

    private String name;
    private String username;
    private String password;
    private String email;
}

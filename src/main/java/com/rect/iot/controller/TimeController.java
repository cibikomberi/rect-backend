package com.rect.iot.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
public class TimeController {
    
    @GetMapping("/time")
    public LocalDateTime getTime() {
        return LocalDateTime.now();
    }
}

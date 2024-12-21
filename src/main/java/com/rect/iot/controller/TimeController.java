package com.rect.iot.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class TimeController {
    
    @GetMapping("/time")
    public LocalDateTime getTime() {
        return LocalDateTime.now();
    }
}

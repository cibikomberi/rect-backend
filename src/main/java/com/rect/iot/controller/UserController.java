package com.rect.iot.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rect.iot.model.User;
import com.rect.iot.service.UserService;



@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    

    @PostMapping("/register")
    public User createUser(@RequestBody ObjectNode user) {
        return userService.createUser(user.get("name").asText(), user.get("email").asText(), user.get("password").asText());
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody ObjectNode user) {
        return userService.login(user.get("email").asText(), user.get("password").asText());
    }
    @PostMapping("/auth/refresh-token")
    public String refreshToken(@RequestBody ObjectNode json) {
        System.out.println("a");
        return userService.refreshToken(json.get("token").asText());
    }

    @PostMapping("/login-vs")
    public Map<String, String> loginVScode(@RequestBody ObjectNode user) {
        return userService.login(user.get("email").asText(), user.get("password").asText());
    }

    @GetMapping("/whoami")
    public User whoAmI() {
        return userService.whoAmI();
    }

    @GetMapping("/whoisthis/{userId}")
    public User whiIsThis(@PathVariable String userId) {
        return userService.whoIsThis(userId);
    }

    @PutMapping("/profile")
    public String updateProfile(@RequestPart JsonNode profile, @RequestPart(required = false) MultipartFile image) throws IOException {
        return userService.updateProfile(profile.get("name").asText(), profile.get("phone").asLong(), image);
    }

    @PostMapping("/profile/resetPassword")
    public String resetPassword(@RequestBody JsonNode json) {        
        return userService.resetPassword(json.get("existingPassword").asText(), json.get("password").asText());
    }
    

    @GetMapping("/profile/image/{id}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable String id) {
        return userService.getProfileImage(id);
    }
    
}

package com.rect.iot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public String login(@RequestBody ObjectNode user) {
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
}

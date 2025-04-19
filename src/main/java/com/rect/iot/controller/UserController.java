package com.rect.iot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rect.iot.model.user.AuthToken;
import com.rect.iot.model.user.User;
import com.rect.iot.service.UserService;
import com.rect.iot.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;



@CrossOrigin
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public User createUser(@RequestBody ObjectNode user) {
        return userService.createUser(user.get("name").asText(), user.get("email").asText(), user.get("password").asText());
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody ObjectNode user, HttpServletRequest req) {
        String userAgent = req.getHeader("user-agent");

        Parser uaParser = new Parser();
        Client client = uaParser.parse(userAgent);
        return userService.login(user.get("email").asText(), user.get("password").asText(), client, HttpUtils.getRequestIP(req));
    }
    @PostMapping("/auth/refresh-token")
    public String refreshToken(@RequestBody ObjectNode json) {
        return userService.refreshToken(json.get("token").asText());
    }

    @PostMapping("/login-vs")
    public Map<String, String> loginVScode(@RequestBody ObjectNode user, HttpServletRequest req) {
        String userAgent = req.getHeader("user-agent");

        Parser uaParser = new Parser();
        Client client = uaParser.parse(userAgent);
        return userService.login(user.get("email").asText(), user.get("password").asText(), client, HttpUtils.getRequestIP(req));
    }

    @GetMapping("/user/sessions")
    public List<AuthToken> getMySessions() {
        return userService.getMySessions();
    }
    
    @PutMapping("/user/session-logout")
    public String logoutSession(@RequestBody ObjectNode data) throws IllegalAccessException {
        return userService.logoutSession(data.get("id").asText());
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

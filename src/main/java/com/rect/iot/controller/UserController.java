package com.rect.iot.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
import com.rect.iot.model.user.AuthToken;
import com.rect.iot.model.user.User;
import com.rect.iot.service.UserService;
import com.rect.iot.utils.HttpUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ua_parser.Client;
import ua_parser.Parser;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public User createUser(@RequestBody ObjectNode user) {
        return userService.createUser(user.get("name").asText(), user.get("email").asText(),
                user.get("password").asText());
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody ObjectNode user, HttpServletRequest req) {
        String userAgent = req.getHeader("user-agent");

        Parser uaParser = new Parser();
        Client client = uaParser.parse(userAgent);
        return userService.login(user.get("email").asText(), user.get("password").asText(), client,
                HttpUtils.getRequestIP(req));
    }

    @GetMapping("/oauth/success")
    public void oauthLogin(
            @AuthenticationPrincipal OAuth2User user,
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, IllegalAccessException {

        String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
        String userAgent = request.getHeader("user-agent");

        Parser uaParser = new Parser();
        Client client = uaParser.parse(userAgent);

        var tokens = userService.oauthLogin(user, authorizedClient, client, HttpUtils.getRequestIP(request));
        response.sendRedirect(redirectUrl + "?jwt=" + tokens.get("jwt") + "&authToken=" + tokens.get("authToken"));
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
        return userService.login(user.get("email").asText(), user.get("password").asText(), client,
                HttpUtils.getRequestIP(req));
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
    public String updateProfile(@RequestPart JsonNode profile, @RequestPart(required = false) MultipartFile image)
            throws IOException {
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

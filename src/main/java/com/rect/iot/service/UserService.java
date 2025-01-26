package com.rect.iot.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.model.AuthToken;
import com.rect.iot.model.Image;
import com.rect.iot.model.User;
import com.rect.iot.model.UserPrincipal;
import com.rect.iot.repository.AuthTokenRepo;
import com.rect.iot.repository.ImageRepo;
import com.rect.iot.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ImageRepo imageRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private AuthTokenRepo authTokenRepo;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public User createUser(String name, String email, String password) {
        User existingUser = userRepo.findByEmail(email);
        if (existingUser == null) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(encoder.encode(password));
            user.setSharedDevices(new HashSet<>());
            user.setSharedTemplates(new HashSet<>());
            user.setSharedDashboards(new HashSet<>());

            return userRepo.save(user);
        }
        throw new DuplicateKeyException("User already exists");
    }

    public Map<String, String> login(String email, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        if (authentication.isAuthenticated()) {
            String userId = ((UserPrincipal) authentication.getPrincipal()).getId();
            String jwt = jwtService.generateToken(email, userId,
                    ((UserPrincipal) authentication.getPrincipal()).getRole());
            String authToken = jwtService.generateAuthToken(email, userId,
                    ((UserPrincipal) authentication.getPrincipal()).getRole());

            authTokenRepo.save(AuthToken.builder()
                    .token(authToken)
                    .userId(userId)
                    .build());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("jwt", jwt);
            tokens.put("authToken", authToken);
            return tokens;
        }
        System.out.println("fail");
        return null;
    }

    public String refreshToken(String token) {
        System.out.println(token);
        AuthToken authToken = authTokenRepo.findByToken(token);
        if (authToken != null) {
            return jwtService.generateToken(
                    jwtService.extractUserName(token),
                    jwtService.extractId(token),
                    jwtService.extracRole(token));
        }
        System.out.println("fail");
        return null;
    }

    public User whoAmI() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();
        User user = userRepo.findById(userId).get();
        return user;
    }

    public User whoIsThis(String userId) {
        User user = userRepo.findById(userId).get();
        return user;
    }

    public String getMyUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getPrincipal();
    }

    public String updateProfile(String name, Long phone, MultipartFile image) throws IOException {
        User user = whoAmI();
        if (image != null) {
            if (user.getImageId() == null) {
                Image profileImage = new Image();
                profileImage.setImageType(image.getContentType());
                profileImage.setContent(image.getBytes());
                user.setImageId(imageRepo.save(profileImage).getId());
            } else {
                Image profileImage = imageRepo.findById(user.getImageId()).get();
                profileImage.setImageType(image.getContentType());
                profileImage.setContent(image.getBytes());
                imageRepo.save(profileImage);
            }
        }

        user.setName(name);
        user.setPhone(phone);
        userRepo.save(user);
        return "ok";
    }

    public String resetPassword(String existingPassword, String newPassword) {
        User user = whoAmI();
        if (encoder.matches(existingPassword, user.getPassword())) {
            user.setPassword(encoder.encode(newPassword));
            userRepo.save(user);
            return "ok";
        }
        return "wrong password";
    }

    public ResponseEntity<byte[]> getProfileImage(String id) {
        Image image = imageRepo.findById(id).get();
        return ResponseEntity.ok().contentType(MediaType.valueOf(image.getImageType())).body(image.getContent());
    }
}

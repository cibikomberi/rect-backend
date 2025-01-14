package com.rect.iot.service;

import java.io.IOException;
import java.util.HashSet;

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

import com.rect.iot.model.Image;
import com.rect.iot.model.User;
import com.rect.iot.model.UserPrincipal;
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

    public String login(String email, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        if (authentication.isAuthenticated()) {
            System.out.println();
            return jwtService.generateToken(email, ((UserPrincipal) authentication.getPrincipal()).getId());
        }
        System.out.println("fail");
        return "fail";
    }

    public User whoAmI() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepo.findByEmail(principal.getEmail());
        return user;
    }

    public User whoIsThis(String userId) {
        User user = userRepo.findById(userId).get();
        return user;
    }

    public String getMyUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getId();
    }

    public String updateProfile(String name, Long phone, MultipartFile image) throws IOException {
        User user = whoAmI();
        if (image != null) {
            if ( user.getImageId() == null) {
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

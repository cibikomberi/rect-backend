package com.rect.iot.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.rect.iot.model.Image;
import com.rect.iot.model.user.AuthToken;
import com.rect.iot.model.user.User;
import com.rect.iot.model.user.UserPrincipal;
import com.rect.iot.repository.AuthTokenRepo;
import com.rect.iot.repository.ImageRepo;
import com.rect.iot.repository.UserRepo;
import lombok.RequiredArgsConstructor;
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
import ua_parser.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepo userRepo;
    private final ImageRepo imageRepo;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AuthTokenRepo authTokenRepo;
    private final DatabaseReader reader;

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

    public Map<String, String> login(String email, String password, Client client, String ipAddress) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        if (authentication.isAuthenticated()) {
            String userId = ((UserPrincipal) authentication.getPrincipal()).getId();
            String jwt = jwtService.generateToken(email, userId,
                    ((UserPrincipal) authentication.getPrincipal()).getRole());
            String authToken = jwtService.generateAuthToken(email, userId,
                    ((UserPrincipal) authentication.getPrincipal()).getRole());

            String location;

            try{
                System.out.println(ipAddress);
                InetAddress ip = InetAddress.getByName("152.58.249.13");
                CityResponse response = reader.city(ip);
                location = response.getCity().getName() + ", " + response.getMostSpecificSubdivision().getName() + ", " + response.getCountry().getName();
            } catch (Exception e) {
                location = "Unknown location";
            }

            authTokenRepo.save(AuthToken.builder()
                .os(client.os.family)
                .client(client.userAgent.family)
                .location(location)
                .lastActiveTime(LocalDateTime.now())
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
            authToken.setLastActiveTime(LocalDateTime.now());
            authTokenRepo.save(authToken);
            return jwtService.generateToken(
                    jwtService.extractUserName(token),
                    jwtService.extractId(token),
                    jwtService.extracRole(token));
        }
        System.out.println("fail");
        return null;
    }

    public List<AuthToken> getMySessions() {
        String userId = getMyUserId();
        List<AuthToken> authTokens = authTokenRepo.findByUserId(userId);
        return authTokens;
    }
    
    public String logoutSession(String id) throws IllegalAccessException {
        String userId = getMyUserId();
        System.out.println(userId);
        System.out.println(id);
        AuthToken token = authTokenRepo.deleteByUserIdAndId(userId, id);
        if (token != null) {
            return "ok";
        }
        throw new IllegalAccessException("Unauthorised user");
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

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
import com.rect.iot.utils.GithubUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
        email = email.toLowerCase().trim();
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
        email = email.toLowerCase().trim();
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        if (authentication.isAuthenticated()) {
            String userId = ((UserPrincipal) authentication.getPrincipal()).getId();
            String jwt = jwtService.generateToken(email, userId,
                    ((UserPrincipal) authentication.getPrincipal()).getRole());
            String authToken = jwtService.generateAuthToken(email, userId,
                    ((UserPrincipal) authentication.getPrincipal()).getRole());

            String location;

            try {
                InetAddress ip = InetAddress.getByName(ipAddress);
                CityResponse response = reader.city(ip);
                location = response.getCity().getName() + ", " + response.getMostSpecificSubdivision().getName() + ", "
                        + response.getCountry().getName();
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
        return null;
    }

    public Map<String, String> oauthLogin(OAuth2User oAuth2User, OAuth2AuthorizedClient authorizedClient, Client client,
            String ipAddress)
            throws IllegalAccessException {
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            email = GithubUtil.fetchEmail(oAuth2User, authorizedClient);
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setName(oAuth2User.getAttribute("name"));
            user.setEmail(email);
            user.setSharedDashboards(new HashSet<>());
            user.setSharedTemplates(new HashSet<>());
            user.setSharedDevices(new HashSet<>());

            if (oAuth2User.getAttribute("picture") != null) {
                user.setImageUrl(oAuth2User.getAttribute("picture"));
            } else if (oAuth2User.getAttribute("avatar_url") != null) {
                user.setImageUrl(oAuth2User.getAttribute("avatar_url"));
            }

            user = userRepo.save(user);
        }
        String jwt = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());
        String authToken = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());

        String location;
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            CityResponse response = reader.city(ip);
            location = response.getCity().getName() + ", " + response.getMostSpecificSubdivision().getName() + ", "
                    + response.getCountry().getName();
        } catch (Exception e) {
            location = "Unknown location";
        }
        authTokenRepo.save(AuthToken.builder()
                .os(client.os.family)
                .client(client.userAgent.family)
                .location(location)
                .lastActiveTime(LocalDateTime.now())
                .token(authToken)
                .userId(user.getId())
                .build());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("jwt", jwt);
        tokens.put("authToken", authToken);
        return tokens;
    }

    public String refreshToken(String token) {
        AuthToken authToken = authTokenRepo.findByToken(token);
        if (authToken != null) {
            authToken.setLastActiveTime(LocalDateTime.now());
            authTokenRepo.save(authToken);
            return jwtService.generateToken(
                    jwtService.extractUserName(token),
                    jwtService.extractId(token),
                    jwtService.extracRole(token));
        }
        return null;
    }

    public List<AuthToken> getMySessions() {
        String userId = getMyUserId();
        List<AuthToken> authTokens = authTokenRepo.findByUserId(userId);
        return authTokens;
    }

    public String logoutSession(String id) throws IllegalAccessException {
        String userId = getMyUserId();
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

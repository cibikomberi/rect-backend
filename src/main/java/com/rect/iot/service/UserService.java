package com.rect.iot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rect.iot.model.User;
import com.rect.iot.model.UserPrincipal;
import com.rect.iot.repository.UserRepo;


@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
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
            return userRepo.save(user);
        }
        throw new DuplicateKeyException("User already exists");
    }

    public String login(String email, String password) {
        System.out.println(password);
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(email);
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
}

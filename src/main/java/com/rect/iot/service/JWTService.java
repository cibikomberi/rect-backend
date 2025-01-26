package com.rect.iot.service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service
public class JWTService {
    private String secretKey = "Kygqrhqwn0W8E5j8vAyaq7KIza1WqzCjf2FgLbPepYg=";

    public JWTService() {
        // try {
        //     KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        //     SecretKey key = keyGenerator.generateKey();
        //     secretKey = Base64.getEncoder().encodeToString(key.getEncoded());
        // } catch (NoSuchAlgorithmException e) {
            
        // }
    }
    public String generateToken(String username, String id, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);
        System.out.println("s"+secretKey);

        return Jwts.builder()
            .claims()
            .add(claims)
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
            .and()
            .signWith(getKey())
            .compact();
    }

    public String generateAuthToken(String username, String id, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);
        System.out.println("s"+secretKey);

        return Jwts.builder()
            .claims()
            .add(claims)
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365))
            .and()
            .signWith(getKey())
            .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // JWTService - Extract roles/claims from token
    public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<String> role = List.of((String) claims.get("role"));
        return role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }


    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    public String extractId(String token) {
        final Claims claims = extractAllClaims(token);
        return (String) claims.get("id");
    }

    public String extracRole(String token) {
        final Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}

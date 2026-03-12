package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // ⚠️ Production da environment variable dan oling
    private static final String SECRET = "FR3DSecretKey2024MustBe256BitsLongAtMinimum!!";
    private static final long   EXPIRY = 7L * 24 * 60 * 60 * 1000; // 7 kun (ms)

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Token yaratish: userId va username saqlanadi
    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token ichidan username olish
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Token ichidan userId olish
    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    // Token hali amal qiladi?
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
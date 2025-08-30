package com.film.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expirationMs;

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(CustomUserDetails userDetails) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .claim("role", userDetails.getRole().name())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(getSigningKey())
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public String extractRole(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("token", String.class);
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}

package com.example.googleAuth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String extractUsername(String token) throws ExpiredJwtException {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) throws ExpiredJwtException {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) throws ExpiredJwtException {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 1000)) // 30 seconds
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String username) throws ExpiredJwtException {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
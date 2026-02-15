package com.autorization.autorization.security.util;

import com.autorization.autorization.auth.domain.model.user.UserDomain;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        // Usa Keys.hmacShaKeyFor directamente con el secret codificado
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDomain user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        String email = user.getEmail().value();

        // Mapeo limpio de roles y permisos
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().value())
                .collect(Collectors.toList());

        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getName().value())
                .distinct()
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(email) // El email VA en el sub
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("userId", user.getUserId().id())
                .claim("email",user.getEmail().value())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    // Método robusto sin reflexión
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        // Si el subject es el email, esto basta
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
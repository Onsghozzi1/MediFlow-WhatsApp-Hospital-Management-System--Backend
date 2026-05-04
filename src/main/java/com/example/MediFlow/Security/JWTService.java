package com.example.MediFlow.Security;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTService {
    @Value("${token.secret.key}")
    private String jwtSecretKey;
    @Autowired
    UserRepository ur;

    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        User user = ur.findUserByEmail(email).get();
        claims.put("roles", user.getRoleTypes());
        claims.put("email", user.getEmail());
        return createToken(claims, email);
    }

    public List<String> getRoleFromToken(String token) {
        List<String> roles = extractAllClaims(token).get("roles", List.class);
        if (roles != null && !roles.isEmpty()) {
            return roles; // Return the first role in the list
        } else {
            return null; // Or handle the absence of roles differently based on your use case
        }
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 120))
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Use signWith(SignatureAlgorithm, Key)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
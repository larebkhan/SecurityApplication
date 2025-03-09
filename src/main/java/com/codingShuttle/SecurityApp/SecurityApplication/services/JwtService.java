package com.codingShuttle.SecurityApp.SecurityApplication.services;

import com.codingShuttle.SecurityApp.SecurityApplication.entities.Session;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import com.codingShuttle.SecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.codingShuttle.SecurityApp.SecurityApplication.repositories.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;
    private final SessionRepository sessionRepository;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    @Transactional
     public String generateAccessToken(User user){
         String token =  Jwts.builder()
                 .subject(user.getId().toString())
                 .claim("email",user.getEmail())
                 .claim("roles", Set.of("ADMIN","USER"))
                 .issuedAt(new Date())
                 .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
                 .signWith(getSecretKey())
                 .compact();

        // Delete old session if it exists
        sessionRepository.findByUser(user).ifPresent(sessionRepository::delete);


        Session session = new Session();
         session.setToken(token);
         session.setUser(user);
         session.setCreatedAt(LocalDateTime.now());
         sessionRepository.save(session);
         return token;

     }

    @Transactional
    public String generateRefreshToken(User user){
        String token =  Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L *60*60*24*30*6))
                .signWith(getSecretKey())
                .compact();

        // Delete old session if it exists
        sessionRepository.findByUser(user).ifPresent(sessionRepository::delete);


        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setCreatedAt(LocalDateTime.now());
        sessionRepository.save(session);
        return token;

    }

     public Long getUserIdFromToken(String token){  // this throws the jwt exception like in case the token has expired
         Claims claims = Jwts.parser()
                 .verifyWith(getSecretKey())
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();

         return Long.valueOf(claims.getSubject());
     }
}

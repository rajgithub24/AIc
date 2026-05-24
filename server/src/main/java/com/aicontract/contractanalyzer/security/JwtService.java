package com.aicontract.contractanalyzer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

        private final Key key;

        public JwtService(
                        @Value("${app.jwt.secret-key}") String secretKey) {

                this.key = Keys.hmacShaKeyFor(
                                secretKey.getBytes());
        }

        public String generateToken(String email) {

                return Jwts.builder()
                                .setSubject(email)
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(
                                                                System.currentTimeMillis()
                                                                                + 1000 * 60 * 60 * 10))
                                .signWith(
                                                key,
                                                SignatureAlgorithm.HS256)
                                .compact();
        }

        public String extractUsername(String token) {

                return extractClaims(token)
                                .getSubject();
        }

        public boolean isTokenValid(
                        String token) {

                return extractClaims(token)
                                .getExpiration()
                                .after(new Date());
        }

        private Claims extractClaims(String token) {

                return Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
        }
}

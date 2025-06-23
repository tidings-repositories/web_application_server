package com.delivalue.tidings.common;

import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {
    private final SecretKey key;

    TokenProvider(@Value("${JWT_SECRET_STRING}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJWT(String id, String type) {
        int expirationTime = type.equals("REFRESH") ? 3600 * 24 * 28 : 3600;

        return Jwts.builder()
                .subject(id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(this.key)
                .compact();
    }

    public String getUserId(String token) {
        return Jwts.parser()
                .verifyWith(this.key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validate(String token) {
        try {
            this.getUserId(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.delivalue.tidings.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

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
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime * 1000L))
                .signWith(this.key)
                .compact();
    }

    public Optional<String> extractUserId(String token) {
        return extractUserIdByType(token, "ACCESS");
    }

    public Optional<String> extractRefreshUserId(String token) {
        return extractUserIdByType(token, "REFRESH");
    }

    private Optional<String> extractUserIdByType(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(this.key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!expectedType.equals(claims.get("type", String.class))) {
                return Optional.empty();
            }

            return Optional.ofNullable(claims.getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

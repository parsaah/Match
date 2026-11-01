package org.example.match.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtDenylistService {

    private final StringRedisTemplate redisTemplate;

    public void revoke(Jwt jwt) {

        String jti = jwt.getId();
        String issuer = jwt.getClaimAsString("iss");
        Instant expiresAt = jwt.getExpiresAt();

        if (jti == null || issuer == null || expiresAt == null) {
            throw new IllegalArgumentException(
                    "JWT is missing required claims"
            );
        }

        Duration ttl = Duration.between(
                Instant.now(),
                expiresAt
        );

        if (ttl.isZero() || ttl.isNegative()) {
            return;
        }

        String key = "jwt:denylist:" + issuer + ":" + jti;

        redisTemplate.opsForValue().set(
                key,
                "revoked",
                ttl
        );
    }

    public boolean isRevoked(Jwt jwt) {

        String jti = jwt.getId();
        String issuer = jwt.getClaimAsString("iss");

        if (jti == null || issuer == null) {
            return false;
        }

        String key = "jwt:denylist:" + issuer + ":" + jti;

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }
}
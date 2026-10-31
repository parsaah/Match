package org.example.match.auth.service;

import org.example.match.auth.exception.InvalidRefreshTokenException;
import org.example.match.auth.refresh.RefreshToken;
import org.example.match.auth.refresh.RefreshTokenRepository;

import org.example.match.user.entity.User;
import org.example.match.user.enums.UserStatus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpirationDays;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${security.jwt.refresh-expiration-days}")
            long refreshExpirationDays) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    @Transactional
    public IssuedRefreshToken create(User user) {
        return persistNewToken(user);
    }

    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public RotatedRefreshToken rotate(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }

        String tokenHash = hashToken(rawToken);

        RefreshToken current = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (current.isRevoked()
                || !current.getExpiresAt().isAfter(Instant.now())) {

            throw new InvalidRefreshTokenException();
        }

        User user = current.getUser();

        if (user.getStatus() != UserStatus.ACTIVE) {
            current.setRevoked(true);
            throw new InvalidRefreshTokenException();
        }

        // Invalidate the old token before issuing its replacement.
        current.setRevoked(true);

        IssuedRefreshToken replacement = persistNewToken(user);

        return new RotatedRefreshToken(
                user,
                replacement.token(),
                replacement.expiresAt()
        );
    }

    @Transactional
    public void revoke(String rawToken) {

        // Logout is idempotent: an unknown or already revoked token
        // does not cause an error.
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        String tokenHash = hashToken(rawToken);

        refreshTokenRepository.findByTokenHashForUpdate(tokenHash)
                .ifPresent(token -> token.setRevoked(true));
    }

    private IssuedRefreshToken persistNewToken(User user) {

        byte[] randomBytes = new byte[64];
        SECURE_RANDOM.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        Instant expiresAt = Instant.now()
                .plus(refreshExpirationDays, ChronoUnit.DAYS);

        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(hashToken(rawToken));
        entity.setExpiresAt(expiresAt);
        entity.setRevoked(false);

        refreshTokenRepository.save(entity);

        // Return the raw token only to the caller, never store it.
        return new IssuedRefreshToken(rawToken, expiresAt);
    }

    private String hashToken(String rawToken) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is not available",
                    exception
            );
        }
    }

    public record IssuedRefreshToken(
            String token,
            Instant expiresAt
    ) {
    }

    public record RotatedRefreshToken(
            User user,
            String token,
            Instant expiresAt
    ) {
    }
}

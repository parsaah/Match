package org.example.match.auth.service;

import org.example.match.auth.dto.AuthResponse;
import org.example.match.auth.dto.LoginRequest;
import org.example.match.auth.dto.RegisterRequest;
import org.example.match.auth.dto.RefreshTokenRequest;
import org.example.match.security.CustomUserDetailsService;
import org.example.match.security.JwtDenylistService;
import org.example.match.security.JwtService;

import org.example.match.user.entity.User;
import org.example.match.user.enums.UserRole;
import org.example.match.user.enums.UserStatus;
import org.example.match.user.repository.UserRepository;
import org.example.match.user.exception.UsernameAlreadyExistsException;
import org.example.match.user.exception.EmailAlreadyExistsException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final long accessExpirationMinutes;
    private final JwtDenylistService jwtDenylistService;
    private final JwtDecoder jwtDecoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService,
            JwtDenylistService jwtDenylistService,
            JwtDecoder jwtDecoder,
            @Value("${security.jwt.expiration-minutes}")
            long accessExpirationMinutes) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.jwtDenylistService = jwtDenylistService;
        this.jwtDecoder = jwtDecoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String username = request.username().trim();
        String email = request.email().trim();

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = new User();
        user.setUsername(username);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setAge(request.age());

        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber().trim());
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        // Values are controlled by the server, not the request.
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        savedUser.getUsername()
                );

        RefreshTokenService.IssuedRefreshToken refreshToken =
                refreshTokenService.create(savedUser);

        return createAuthResponse(
                userDetails,
                refreshToken.token(),
                refreshToken.expiresAt()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username().trim(),
                                request.password()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(
                userDetails.getUsername()
        ).orElseThrow(() ->
                new BadCredentialsException("Invalid credentials")
        );

        RefreshTokenService.IssuedRefreshToken refreshToken =
                refreshTokenService.create(user);

        return createAuthResponse(
                userDetails,
                refreshToken.token(),
                refreshToken.expiresAt()
        );
    }

    public AuthResponse refresh(RefreshTokenRequest request) {

        RefreshTokenService.RotatedRefreshToken rotated =
                refreshTokenService.rotate(request.refreshToken());

        // Reload the account so current role and account status are used.
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        rotated.user().getUsername()
                );

        return createAuthResponse(
                userDetails,
                rotated.token(),
                rotated.expiresAt()
        );
    }

    public void logout(
            String refreshToken,
            String accessToken) {

        refreshTokenService.revoke(refreshToken);

        if (accessToken != null && !accessToken.isBlank()) {

            try {
                Jwt jwt = jwtDecoder.decode(accessToken);

                jwtDenylistService.revoke(jwt);

            } catch (JwtException ex) {
                // Access token is invalid or expired.
                // Refresh token has already been revoked.
            }
        }
    }

    private AuthResponse createAuthResponse(
            UserDetails userDetails,
            String refreshToken,
            Instant refreshExpiresAt) {

        String accessToken = jwtService.generateToken(userDetails);

        long refreshExpiresIn = Math.max(
                0,
                Duration.between(
                        Instant.now(),
                        refreshExpiresAt
                ).getSeconds()
        );

        return new AuthResponse(
                accessToken,
                "Bearer",
                accessExpirationMinutes * 60,
                refreshToken,
                refreshExpiresIn
        );
    }
}
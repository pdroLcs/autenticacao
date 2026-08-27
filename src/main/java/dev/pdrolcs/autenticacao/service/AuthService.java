package dev.pdrolcs.autenticacao.service;

import dev.pdrolcs.autenticacao.config.TokenConfig;
import dev.pdrolcs.autenticacao.dto.request.LoginRequest;
import dev.pdrolcs.autenticacao.dto.request.RegisterRequest;
import dev.pdrolcs.autenticacao.dto.response.LoginResponse;
import dev.pdrolcs.autenticacao.dto.response.RegisterResponse;
import dev.pdrolcs.autenticacao.entity.User;
import dev.pdrolcs.autenticacao.exception.EmailAlreadyRegisteredException;
import dev.pdrolcs.autenticacao.exception.InvalidTokenException;
import dev.pdrolcs.autenticacao.repository.RefreshTokenRepository;
import dev.pdrolcs.autenticacao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    @Value("${jwt.refresh.token.expiration}")
    private long expirationRefreshTokenTime;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenConfig tokenConfig, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsUserByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException("This email is already in use");
        }
        var user = new User();
        user.setPublicId();
        user.changeName(request.name());
        user.changeEmail(request.email());
        user.changePassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return new RegisterResponse(user.getName(), user.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        var userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var authentication = authenticationManager.authenticate(userAndPass);
        var user = (User) authentication.getPrincipal();
        var accessToken = tokenConfig.generateAccessToken(user);
        var refreshToken = tokenConfig.generateRefreshToken(user);
        refreshTokenService.save(user, refreshToken, Instant.now().plusSeconds(expirationRefreshTokenTime));
        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse refresh(String refreshToken) {
        tokenConfig.validateRefreshToken(refreshToken);
        var storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        if (storedToken.isRevoked()) {
            throw new InvalidTokenException("Token is revoked");
        }
        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token is expired");
        }
        var user = storedToken.getUser();

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        var newAccessToken = tokenConfig.generateAccessToken(user);
        var newRefreshToken = tokenConfig.generateRefreshToken(user);
        refreshTokenService.save(user, newRefreshToken, Instant.now().plusSeconds(expirationRefreshTokenTime));

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        tokenConfig.validateRefreshToken(refreshToken);
        var storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
    }
}

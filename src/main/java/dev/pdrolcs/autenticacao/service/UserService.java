package dev.pdrolcs.autenticacao.service;

import dev.pdrolcs.autenticacao.config.TokenConfig;
import dev.pdrolcs.autenticacao.dto.request.LoginRequest;
import dev.pdrolcs.autenticacao.dto.request.RegisterRequest;
import dev.pdrolcs.autenticacao.dto.response.LoginResponse;
import dev.pdrolcs.autenticacao.dto.response.RegisterResponse;
import dev.pdrolcs.autenticacao.entity.User;
import dev.pdrolcs.autenticacao.exception.EmailAlreadyRegisteredException;
import dev.pdrolcs.autenticacao.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
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
        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse refresh(String refreshToken) {
        var decodedToken = tokenConfig.validateRefreshToken(refreshToken);
        var publicId = UUID.fromString(decodedToken.getClaim("userId").asString());
        var user = userRepository.findUserByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var newAccessToken = tokenConfig.generateAccessToken((User) user);
        return new LoginResponse(newAccessToken, refreshToken);
    }
}

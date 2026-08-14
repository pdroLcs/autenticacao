package dev.pdrolcs.autenticacao.service;

import dev.pdrolcs.autenticacao.dto.request.LoginRequest;
import dev.pdrolcs.autenticacao.dto.request.RegisterRequest;
import dev.pdrolcs.autenticacao.dto.response.LoginResponse;
import dev.pdrolcs.autenticacao.dto.response.RegisterResponse;
import dev.pdrolcs.autenticacao.entity.User;
import dev.pdrolcs.autenticacao.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        var user = new User();
        user.setPublicId();
        user.changeName(request.name());
        user.changeEmail(request.email());
        user.changePassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return new RegisterResponse(user.getName(), user.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        return null;
    }
}

package dev.pdrolcs.autenticacao.controller;

import dev.pdrolcs.autenticacao.docs.AuthControllerDoc;
import dev.pdrolcs.autenticacao.dto.request.LoginRequest;
import dev.pdrolcs.autenticacao.dto.request.RegisterRequest;
import dev.pdrolcs.autenticacao.dto.response.LoginHttpResponse;
import dev.pdrolcs.autenticacao.dto.response.RegisterResponse;
import dev.pdrolcs.autenticacao.exception.InvalidTokenException;
import dev.pdrolcs.autenticacao.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerDoc {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginHttpResponse> login(@Valid @RequestBody LoginRequest request) {
        var response = authService.login(request);
        var refreshCookie = ResponseCookie
                .from("refresh_token", response.refreshToken())
                .httpOnly(true)
                .secure(false) // Set to true in production
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginHttpResponse(response.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginHttpResponse> refresh(@CookieValue("refresh_token") String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new InvalidTokenException("Refresh token is missing");
        }
        var response = authService.refresh(refreshToken);
        var refreshCookie = ResponseCookie
                .from("refresh_token", response.refreshToken())
                .httpOnly(true)
                .secure(false) // Set to true in production
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginHttpResponse(response.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("refresh_token") String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new InvalidTokenException("Refresh token is missing");
        }
        authService.logout(refreshToken);

        var cookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // Set to true in production
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.
                noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

}

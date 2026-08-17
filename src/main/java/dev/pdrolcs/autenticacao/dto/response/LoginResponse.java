package dev.pdrolcs.autenticacao.dto.response;

public record LoginResponse(String accessToken, String refreshToken) {
}

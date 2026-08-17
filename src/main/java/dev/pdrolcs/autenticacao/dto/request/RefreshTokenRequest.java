package dev.pdrolcs.autenticacao.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RefreshTokenRequest(

        @NotEmpty(message = "Refresh token cannot be empty")
        String refreshToken
) {
}

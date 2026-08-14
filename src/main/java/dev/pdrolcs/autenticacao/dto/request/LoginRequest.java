package dev.pdrolcs.autenticacao.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must be less than 100 characters")
        @NotEmpty(message = "Email cannot be empty")
        String email,

        @Size(min = 4, max = 255, message = "Password must be between 4 and 255 characters")
        @NotEmpty(message = "Password cannot be empty")
        String password
) {
}

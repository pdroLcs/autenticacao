package dev.pdrolcs.autenticacao.docs;

import dev.pdrolcs.autenticacao.dto.request.LoginRequest;
import dev.pdrolcs.autenticacao.dto.request.RegisterRequest;
import dev.pdrolcs.autenticacao.dto.response.LoginHttpResponse;
import dev.pdrolcs.autenticacao.dto.response.LoginResponse;
import dev.pdrolcs.autenticacao.dto.response.RegisterResponse;
import dev.pdrolcs.autenticacao.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth Controller", description = "Endpoints for user authentication and management")
public interface AuthControllerDoc {

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with the provided name, email, and password.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = RegisterResponse.class),
                            examples = @ExampleObject(
                                    name = "RegisterResponse Example",
                                    value = """
                                            {
                                                "name": "Pedro",
                                                "email": "pedro@email.com"
                                            }
                                            """
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse Example",
                                    value = """
                                            {
                                                "timestamp": "2026-01-01T00:00:00Z",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "messages": [
                                                    "name: Name must be between 3 and 50 characters",
                                                    "name: Name cannot be empty",
                                                    "email: Email should be valid",
                                                    "email: Email must be less than 100 characters",
                                                    "email: Email cannot be empty",
                                                    "password: Password must be between 4 and 255 characters",
                                                    "password: Password cannot be empty"
                                                ]
                                            }
                                            """
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email already registered",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse Example",
                                    value = """
                                            {
                                                "timestamp": "2026-01-01T00:00:00Z",
                                                "status": 409,
                                                "error": "Conflict",
                                                "messages": [
                                                    "This email is already in use"
                                                ]
                                            }
                                            """
                            ))
                    )
            }
    )
    ResponseEntity<RegisterResponse> register(
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(
                            name = "RegisterRequest Example",
                            value = """
                                    {
                                        "name": "Pedro",
                                        "email": "pedro@email.com",
                                        "password": "123456"
                                    }
                                    """
                    ))
            )
            RegisterRequest request);

    @Operation(
            summary = "Login a user",
            description = "Authenticates a user with the provided email and password, returning access and refresh tokens.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User logged in successfully",
                            content = @Content(schema = @Schema(implementation = LoginHttpResponse.class),
                            examples = @ExampleObject(
                                    name = "LoginHttpResponse Example",
                                    value = """
                                            {
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                            }
                                            """
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse Example",
                                    value = """
                                            {
                                                "timestamp": "2026-01-01T00:00:00Z",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "messages": [
                                                    "email: Email should be valid",
                                                    "email: Email must be less than 100 characters",
                                                    "email: Email cannot be empty",
                                                    "password: Password must be between 4 and 255 characters",
                                                    "password: Password cannot be empty"
                                                ]
                                            }
                                            """
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse Example",
                                    value = """
                                            {
                                                "timestamp": "2026-01-01T00:00:00Z",
                                                "status": 401,
                                                "error": "Unauthorized",
                                                "messages": [
                                                    "Bad credentials"
                                                ]
                                            }
                                            """
                            ))
                    )
            }
    )
    ResponseEntity<LoginHttpResponse> login(
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                            name = "LoginRequest Example",
                            value = """
                                    {
                                        "email": "pedro@email.com",
                                        "password": "123456"
                                    }
                                    """
                    ))
            )
            LoginRequest request);

    @Operation(
            summary = "Refresh access token",
            description = "Refreshes the access token using a valid refresh token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Access token refreshed successfully",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(
                                    name = "LoginResponse Example",
                                    value = """
                                            {
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                            }
                                            """
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse Example",
                                    value = """
                                            {
                                                "timestamp": "2026-01-01T00:00:00Z",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "messages": [
                                                    "refreshToken: Refresh token cannot be empty"
                                                ]
                                            }
                                            """
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid refresh token",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse Example",
                                    value = """
                                            {
                                                "timestamp": "2026-01-01T00:00:00Z",
                                                "status": 401,
                                                "error": "Unauthorized",
                                                "messages": [
                                                    "Invalid refresh token"
                                                ]
                                            }
                                            """
                            ))
                    )
            }
    )
    ResponseEntity<LoginHttpResponse> refresh(
            @Parameter(
                    name = "refresh_token",
                    description = "Refresh token stored in a HttpOnly cookie",
                    required = true,
                    in = ParameterIn.COOKIE,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @CookieValue("refresh_token")
            String refreshToken);

    @Operation(
            summary = "Logout a user",
            description = "Logs out a user by revoking the provided refresh token.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User logged out successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse Example",
                                    value = """
                                            {
                                                "timestamp": "2026-01-01T00:00:00Z",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "messages": [
                                                    "refreshToken: Refresh token cannot be empty"
                                                ]
                                            }
                                            """
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid refresh token",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse Example",
                                    value = """
                                            {
                                                "timestamp": "2026-01-01T00:00:00Z",
                                                "status": 401,
                                                "error": "Unauthorized",
                                                "messages": [
                                                    "Invalid refresh token"
                                                ]
                                            }
                                            """
                            ))
                    )
            }
    )
    ResponseEntity<Void> logout(
            @Parameter(
                    name = "refresh_token",
                    description = "Refresh token stored in a HttpOnly cookie",
                    required = true,
                    in = ParameterIn.COOKIE,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @CookieValue("refresh_token")
            String refreshToken);
}

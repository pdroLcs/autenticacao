package dev.pdrolcs.autenticacao.service;

import dev.pdrolcs.autenticacao.config.TokenConfig;
import dev.pdrolcs.autenticacao.dto.request.LoginRequest;
import dev.pdrolcs.autenticacao.dto.request.RegisterRequest;
import dev.pdrolcs.autenticacao.dto.response.LoginResponse;
import dev.pdrolcs.autenticacao.dto.response.RegisterResponse;
import dev.pdrolcs.autenticacao.entity.User;
import dev.pdrolcs.autenticacao.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenConfig tokenConfig;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Nested
    @DisplayName("register")
    class RegisterTests {

        private RegisterRequest registerRequest;

        @BeforeEach
        void setUp() {
            registerRequest = new RegisterRequest("Pedro", "pedro@email.com", "password123");
        }

        @Test
        @DisplayName("should register a new user successfully")
        void shouldRegisterUserSuccessfully() {
            String encodedPassword = "encodedPassword123";
            when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);

            RegisterResponse response = userService.register(registerRequest);

            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertThat(response.name()).isEqualTo("Pedro");
            assertThat(response.email()).isEqualTo("pedro@email.com");
            assertThat(savedUser.getName()).isEqualTo("Pedro");
            assertThat(savedUser.getEmail()).isEqualTo("pedro@email.com");
            assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
            assertThat(savedUser.getPublicId()).isNotNull();
        }

        @Test
        @DisplayName("should encode password before saving")
        void shouldEncodePasswordBeforeSaving() {
            String rawPassword = "password123";
            String encodedPassword = "encodedPassword123";
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

            userService.register(registerRequest);

            verify(passwordEncoder).encode(rawPassword);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo(encodedPassword);
        }

        @Test
        @DisplayName("should generate unique public ID for each registration")
        void shouldGenerateUniquePublicId() {
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");

            userService.register(registerRequest);

            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertThat(savedUser.getPublicId()).isNotNull();
        }

        @Test
        @DisplayName("should save user in repository")
        void shouldSaveUserInRepository() {
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");

            userService.register(registerRequest);

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should return RegisterResponse with name and email")
        void shouldReturnRegisterResponseWithCorrectData() {
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");

            RegisterResponse response = userService.register(registerRequest);

            assertThat(response)
                    .isNotNull()
                    .extracting("name", "email")
                    .containsExactly("Pedro", "pedro@email.com");
        }
    }

    @Nested
    @DisplayName("login")
    class LoginTests {

        private LoginRequest loginRequest;
        private User authenticatedUser;
        private String generatedToken;

        @BeforeEach
        void setUp() {
            loginRequest = new LoginRequest("pedro@email.com", "password123");
            authenticatedUser = new User();
            authenticatedUser.changeName("Pedro");
            authenticatedUser.changeEmail("pedro@email.com");
            authenticatedUser.changePassword("encodedPassword");
            generatedToken = "jwt-token-123";
        }

        @Test
        @DisplayName("should authenticate user and return token")
        void shouldAuthenticateUserAndReturnToken() {
            var authentication = mock(org.springframework.security.core.Authentication.class);
            when(authentication.getPrincipal()).thenReturn(authenticatedUser);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(tokenConfig.generateToken(authenticatedUser)).thenReturn(generatedToken);

            LoginResponse response = userService.login(loginRequest);

            assertThat(response.token()).isEqualTo(generatedToken);
        }

        @Test
        @DisplayName("should authenticate with correct email and password")
        void shouldAuthenticateWithCorrectCredentials() {
            var authentication = mock(org.springframework.security.core.Authentication.class);
            when(authentication.getPrincipal()).thenReturn(authenticatedUser);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(tokenConfig.generateToken(authenticatedUser)).thenReturn(generatedToken);

            userService.login(loginRequest);

            ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(tokenCaptor.capture());

            UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
            assertThat(capturedToken.getPrincipal()).isEqualTo("pedro@email.com");
            assertThat(capturedToken.getCredentials()).isEqualTo("password123");
        }

        @Test
        @DisplayName("should generate token with authenticated user")
        void shouldGenerateTokenWithAuthenticatedUser() {
            var authentication = mock(org.springframework.security.core.Authentication.class);
            when(authentication.getPrincipal()).thenReturn(authenticatedUser);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(tokenConfig.generateToken(authenticatedUser)).thenReturn(generatedToken);

            userService.login(loginRequest);

            verify(tokenConfig).generateToken(authenticatedUser);
        }

        @Test
        @DisplayName("should return LoginResponse with generated token")
        void shouldReturnLoginResponseWithToken() {
            var authentication = mock(org.springframework.security.core.Authentication.class);
            when(authentication.getPrincipal()).thenReturn(authenticatedUser);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(tokenConfig.generateToken(authenticatedUser)).thenReturn(generatedToken);

            LoginResponse response = userService.login(loginRequest);

            assertThat(response)
                    .isNotNull()
                    .extracting("token")
                    .isEqualTo(generatedToken);
        }
    }
}

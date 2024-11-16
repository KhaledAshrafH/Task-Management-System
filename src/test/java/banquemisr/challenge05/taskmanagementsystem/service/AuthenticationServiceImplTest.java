package banquemisr.challenge05.taskmanagementsystem.service;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.LoginRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.RegistrationRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.AuthenticationResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.Token;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.UserRole;
import banquemisr.challenge05.taskmanagementsystem.exception.EmailAlreadyExistsException;
import banquemisr.challenge05.taskmanagementsystem.exception.InvalidInputException;
import banquemisr.challenge05.taskmanagementsystem.exception.InvalidTokenException;
import banquemisr.challenge05.taskmanagementsystem.exception.UsernameAlreadyExistsException;
import banquemisr.challenge05.taskmanagementsystem.repository.TokenRepository;
import banquemisr.challenge05.taskmanagementsystem.repository.UserRepository;
import banquemisr.challenge05.taskmanagementsystem.security.JwtTokenService;
import banquemisr.challenge05.taskmanagementsystem.service.NotificationService;
import banquemisr.challenge05.taskmanagementsystem.service.impl.AuthenticationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceImplTest {
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private JwtTokenService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private NotificationService notificationService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .username("test")
                .email("test@yahoo.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .role(UserRole.USER)
                .build();
    }

    @Test
    void register_Success() {
        // Arrange
        RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO();
        registrationRequest.setUsername("test");
        registrationRequest.setEmail("test@yahoo.com");
        registrationRequest.setFirstName("Test");
        registrationRequest.setLastName("User");
        registrationRequest.setPassword("password");

        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthenticationResponseDTO response = authenticationService.register(registrationRequest);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals("test", response.getUsername());
        Assertions.assertEquals("jwtToken", response.getAccessToken());

        verify(notificationService).sendNotification(any());
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void register_UsernameAlreadyExists() {
        // Arrange
        RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO();
        registrationRequest.setUsername("existingUser");
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setFirstName("Test");
        registrationRequest.setLastName("User");
        registrationRequest.setPassword("password");

        // Mock
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> authenticationService.register(registrationRequest));
    }

    @Test
    void register_EmailAlreadyExists() {
        // Arrange
        RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO();
        registrationRequest.setUsername("newUser");
        registrationRequest.setEmail("existing@example.com");
        registrationRequest.setFirstName("Test");
        registrationRequest.setLastName("User");
        registrationRequest.setPassword("password");

        // Mock
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> authenticationService.register(registrationRequest));
    }

    @Test
    void register_InvalidInput() {
        // Arrange
        RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO();

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authenticationService.register(registrationRequest));
    }

    @Test
    void login_Success() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("test");
        loginRequest.setPassword("password");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthenticationResponseDTO response = authenticationService.login(loginRequest);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals("test", response.getUsername());
        Assertions.assertEquals("jwtToken", response.getAccessToken());

        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void login_UserNotFound() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("nonExistentUser");
        loginRequest.setPassword("password");

        // Mock
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.login(loginRequest));
    }

    @Test
    void login_InvalidPassword()  {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("wrongPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("Invalid password")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authenticationService.login(loginRequest));
    }

    @Test
    void logout_Success() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer jwtToken");
        when(jwtService.extractUsername("jwtToken")).thenReturn("testUser ");
        when(userRepository.findByUsername("testUser ")).thenReturn(Optional.of(user));

        // Mock
        Token validToken = new Token();
        validToken.setId(1L);
        validToken.setUser(user);
        validToken.setRevoked(false);
        validToken.setExpired(false);

        when(tokenRepository.findAllValidTokensByUserId(user.getId())).thenReturn(Collections.singletonList(validToken));

        // Act
        authenticationService.logout(request);

        // Assert
        verify(tokenRepository).findAllValidTokensByUserId(user.getId());
        verify(tokenRepository).save(any(Token.class)); // Ensure tokens are revoked
    }

    @Test
    void logout_InvalidToken() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtService.extractUsername("invalidToken")).thenThrow(new InvalidTokenException("Invalid token"));

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> authenticationService.logout(request));
    }

    @Test
    void logout_NoTokenProvided() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        authenticationService.logout(request);

        // Assert
        verify(tokenRepository, never()).findAllValidTokensByUserId(anyLong());
    }
}

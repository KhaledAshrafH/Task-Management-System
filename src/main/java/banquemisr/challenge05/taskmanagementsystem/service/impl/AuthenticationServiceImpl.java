package banquemisr.challenge05.taskmanagementsystem.service.impl;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.LoginRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.NotificationCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.RegistrationRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.AuthenticationResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.Token;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.NotificationType;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TokenType;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.UserRole;
import banquemisr.challenge05.taskmanagementsystem.repository.TokenRepository;
import banquemisr.challenge05.taskmanagementsystem.repository.UserRepository;
import banquemisr.challenge05.taskmanagementsystem.security.JwtTokenService;
import banquemisr.challenge05.taskmanagementsystem.service.AuthenticationService;
import banquemisr.challenge05.taskmanagementsystem.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtTokenService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final NotificationService notificationService;

    @Override
    public AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequestDTO) {
        User user = createUserFromRegistration(registrationRequestDTO);
        User savedUser = userRepository.save(user);

        sendRegistrationNotification(savedUser);

        String jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        return createAuthenticationResponse(savedUser, jwtToken);
    }

    @Override
    public AuthenticationResponseDTO login(LoginRequestDTO loginRequestDTO) {
        authenticate(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

        User user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        sendLoginNotification(user);

        String jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return createAuthenticationResponse(user, jwtToken);
    }


    @Override
    public void logout(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
            revokeAllUserTokens(user);
        }
    }


    private User createUserFromRegistration(RegistrationRequestDTO registrationRequestDTO) {
        return User.builder()
                .username(registrationRequestDTO.getUsername())
                .email(registrationRequestDTO.getEmail())
                .firstName(registrationRequestDTO.getFirstName())
                .lastName(registrationRequestDTO.getLastName())
                .password(passwordEncoder.encode(registrationRequestDTO.getPassword()))
                .role(UserRole.USER)
                .build();
    }

    private void sendRegistrationNotification(User user) {
        NotificationCreationDTO notification = NotificationCreationDTO.builder()
                .userId(user.getId())
                .message("Welcome " + user.getFirstName() + " " + user.getLastName() + ", You have successfully registered.")
                .type(NotificationType.REGISTRATION_SUCCESSFUL)
                .build();
        notificationService.sendNotification(notification);
    }

    private void sendLoginNotification(User user) {
        NotificationCreationDTO notification = NotificationCreationDTO.builder()
                .userId(user.getId())
                .message("Welcome back, " + user.getFirstName() + "!")
                .type(NotificationType.LOGIN_SUCCESSFUL)
                .build();
        notificationService.sendNotification(notification);
    }

    private AuthenticationResponseDTO createAuthenticationResponse(User user, String jwtToken) {
        return AuthenticationResponseDTO.builder()
                .username(user.getUsername())
                .accessToken(jwtToken)
                .build();
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUserId(user.getId());
        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}

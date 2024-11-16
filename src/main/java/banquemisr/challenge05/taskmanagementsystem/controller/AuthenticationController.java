package banquemisr.challenge05.taskmanagementsystem.controller;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.LoginRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.RegistrationRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.AuthenticationResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RequestMapping("api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        AuthenticationResponseDTO response = authenticationService.register(registrationRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        AuthenticationResponseDTO response = authenticationService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.noContent().build();
    }
}

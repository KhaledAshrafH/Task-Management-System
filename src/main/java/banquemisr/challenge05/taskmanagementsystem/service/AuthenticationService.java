package banquemisr.challenge05.taskmanagementsystem.service;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.LoginRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.RegistrationRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Transactional
public interface AuthenticationService {
    AuthenticationResponseDTO register(RegistrationRequestDTO input);
    AuthenticationResponseDTO login(LoginRequestDTO input);
    void logout(HttpServletRequest httpServletRequest);
}

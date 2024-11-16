package banquemisr.challenge05.taskmanagementsystem.controller;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.LoginRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.RegistrationRequestDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.AuthenticationResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.exception.UsernameAlreadyExistsException;
import banquemisr.challenge05.taskmanagementsystem.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthenticationControllerTest {


    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRegister_Success() throws Exception {
        // Arrange
        RegistrationRequestDTO registrationRequestDTO = new RegistrationRequestDTO();
        registrationRequestDTO.setUsername("newUser");
        registrationRequestDTO.setEmail("newuser@example.com");
        registrationRequestDTO.setPassword("12345678");
        registrationRequestDTO.setFirstName("test");
        registrationRequestDTO.setLastName("test");

        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();
        responseDTO.setUsername("newUser");
        responseDTO.setAccessToken("Bearer someToken");

        when(authenticationService.register(registrationRequestDTO)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.accessToken").value("Bearer someToken"));
    }


    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("user");
        loginRequestDTO.setPassword("password");

        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();
        responseDTO.setUsername("user");
        responseDTO.setAccessToken("someToken");

        when(authenticationService.login(any(LoginRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.accessToken").value("someToken"));
    }

    @Test
    public void testLogout_Success() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status(). isNoContent());
        verify(authenticationService, times(1)).logout(any(HttpServletRequest.class));
    }
}
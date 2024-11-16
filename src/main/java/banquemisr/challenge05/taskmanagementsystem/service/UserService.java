package banquemisr.challenge05.taskmanagementsystem.service;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.UserResponseDTO;
import jakarta.transaction.Transactional;
import java.util.List;

@Transactional
public interface UserService {
    List<UserResponseDTO> getAllUsers();
}

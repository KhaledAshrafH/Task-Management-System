package banquemisr.challenge05.taskmanagementsystem.service.impl;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.UserResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.UserRole;
import banquemisr.challenge05.taskmanagementsystem.domain.mapper.UserMapper;
import banquemisr.challenge05.taskmanagementsystem.exception.UnauthorizedAccessException;
import banquemisr.challenge05.taskmanagementsystem.repository.UserRepository;
import banquemisr.challenge05.taskmanagementsystem.service.UserService;
import banquemisr.challenge05.taskmanagementsystem.util.UtilityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        User currentUser = UtilityService.getCurrentUser();
        if(currentUser.getRole()== UserRole.ADMIN){
            List<User> users = userRepository.findAll();
            return userMapper.toResponseDTOs(users);
        }
        else
            throw new UnauthorizedAccessException("You are not allowed to access this service");
    }

}

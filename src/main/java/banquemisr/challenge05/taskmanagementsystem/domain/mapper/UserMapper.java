package banquemisr.challenge05.taskmanagementsystem.domain.mapper;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.UserResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);
    List<UserResponseDTO> toResponseDTOs(List<User> user);
}

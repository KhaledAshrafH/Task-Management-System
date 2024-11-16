package banquemisr.challenge05.taskmanagementsystem.domain.mapper;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskResponseDTO toResponseDTO(Task task);
    List<TaskResponseDTO> toResponseDTOs(List<Task> tasks);

    Task toEntity(TaskCreationDTO taskCreationDTO);
}

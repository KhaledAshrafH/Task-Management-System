package banquemisr.challenge05.taskmanagementsystem.domain.mapper;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskHistoryLogDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskHistoryResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.TaskHistory;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskHistoryMapper {
    TaskHistoryResponseDTO toResponseDTO(TaskHistory taskHistory);
    List<TaskHistoryResponseDTO> toResponseDTOs(List<TaskHistory> taskHistoryList);
    TaskHistory toEntity(TaskHistoryLogDTO taskHistoryLogDTO);
}

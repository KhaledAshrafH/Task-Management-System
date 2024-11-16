package banquemisr.challenge05.taskmanagementsystem.service;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskHistoryLogDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskHistoryResponseDTO;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public interface TaskHistoryService {
    void logTaskHistory(TaskHistoryLogDTO taskHistoryLogDTO);
    List<TaskHistoryResponseDTO> getTaskHistory(Long taskId);
    List<TaskHistoryResponseDTO> getUserTaskHistory();
}

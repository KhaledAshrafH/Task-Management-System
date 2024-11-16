package banquemisr.challenge05.taskmanagementsystem.service.impl;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskHistoryLogDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskHistoryResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.Task;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.TaskHistory;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.UserRole;
import banquemisr.challenge05.taskmanagementsystem.domain.mapper.TaskHistoryMapper;
import banquemisr.challenge05.taskmanagementsystem.exception.TaskNotFoundException;
import banquemisr.challenge05.taskmanagementsystem.exception.UnauthorizedAccessException;
import banquemisr.challenge05.taskmanagementsystem.repository.TaskHistoryRepository;
import banquemisr.challenge05.taskmanagementsystem.repository.TaskRepository;
import banquemisr.challenge05.taskmanagementsystem.service.TaskHistoryService;
import banquemisr.challenge05.taskmanagementsystem.util.UtilityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class TaskHistoryServiceImpl implements TaskHistoryService {
    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskHistoryMapper taskHistoryMapper;
    private static final String TASK_NOT_FOUND_MESSAGE = "Task not found";

    @Override
    @Transactional
    public void logTaskHistory(TaskHistoryLogDTO taskHistoryLogDTO) {
        Task task = taskRepository.findById(taskHistoryLogDTO.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        TaskHistory taskHistory = taskHistoryMapper.toEntity(taskHistoryLogDTO);
        taskHistory.setTask(task);
        taskHistory.setChangedBy(UtilityService.getCurrentUser());
        taskHistory.setChangedAt(LocalDateTime.now());

        TaskHistory savedTaskHistory = taskHistoryRepository.save(taskHistory);
        log.info("Task history logged: {}", savedTaskHistory);
        taskHistoryMapper.toResponseDTO(savedTaskHistory);
    }

    @Override
    public List<TaskHistoryResponseDTO> getTaskHistory(Long taskId) {
        User currentUser = UtilityService.getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        if (currentUser.getRole() == UserRole.ADMIN){
            List<TaskHistory> taskHistoryList = taskHistoryRepository.findAllByTask(task);
            return taskHistoryMapper.toResponseDTOs(taskHistoryList);
        }
        else {
            if (currentUser.getId().equals(task.getAssignedTo().getId())){
                List<TaskHistory> taskHistoryList = taskHistoryRepository.findAllByTask(task);
                return taskHistoryMapper.toResponseDTOs(taskHistoryList);
            }
            else
                throw new UnauthorizedAccessException("You are not allowed to assign this task.");
        }
    }

    @Override
    public List<TaskHistoryResponseDTO> getUserTaskHistory() {
        User currentUser = UtilityService.getCurrentUser();
        List<TaskHistory> taskHistoryList = taskHistoryRepository.findAllByChangedBy(currentUser);
        log.info("Fetched task history for user: {}", currentUser);
        return taskHistoryMapper.toResponseDTOs(taskHistoryList);
    }
}

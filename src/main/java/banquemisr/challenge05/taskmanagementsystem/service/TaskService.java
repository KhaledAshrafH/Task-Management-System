package banquemisr.challenge05.taskmanagementsystem.service;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskSearchCriteriaDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskUpdateDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Transactional
public interface TaskService {
    TaskResponseDTO createTask(TaskCreationDTO taskCreateDTO);
    TaskResponseDTO assignTask(TaskCreationDTO taskCreateDTO);

    TaskResponseDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO);
    TaskResponseDTO getTaskById(Long id);
    Page<TaskResponseDTO> getAllTasks(Pageable pageable);
    Page<TaskResponseDTO> getAllCreatedTasks(Pageable pageable);
    Page<TaskResponseDTO> getAllAssignedTasks(Pageable pageable);
    Page<TaskResponseDTO> getAllAssignedTasksForUser(Long id,Pageable pageable);
    void deleteTaskById(Long id);

    List<TaskResponseDTO> searchAndFilterTasks(TaskSearchCriteriaDTO taskSearchCriteriaDTO);
}

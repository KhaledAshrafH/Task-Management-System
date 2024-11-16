package banquemisr.challenge05.taskmanagementsystem.controller;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskSearchCriteriaDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskUpdateDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskHistoryResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskPriority;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import banquemisr.challenge05.taskmanagementsystem.exception.TaskNotFoundException;
import banquemisr.challenge05.taskmanagementsystem.exception.UnauthorizedAccessException;
import banquemisr.challenge05.taskmanagementsystem.service.TaskHistoryService;
import banquemisr.challenge05.taskmanagementsystem.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;



@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskHistoryService taskHistoryService;


    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskCreationDTO taskCreationDTO) {
        try {
            log.info("Creating task: {}", taskCreationDTO);
            TaskResponseDTO createdTask = taskService.createTask(taskCreationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        }
        catch (UnauthorizedAccessException e) {
            return handleException(e);
        }
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignTask(@Valid @RequestBody TaskCreationDTO taskCreationDTO) {
        try {
            log.info("Assigning task: {}", taskCreationDTO);
            TaskResponseDTO createdTask = taskService.assignTask(taskCreationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        }
        catch (UnauthorizedAccessException | NoSuchElementException e) {
            return handleException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        try {
            log.info("Updating task: {}, {}", id, taskUpdateDTO);
            TaskResponseDTO updatedTask = taskService.updateTask(id, taskUpdateDTO);
            return ResponseEntity.ok(updatedTask);
        }
        catch (UnauthorizedAccessException | TaskNotFoundException e) {
            return handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            log.info("Getting task by ID: {}", id);
            TaskResponseDTO taskResponse = taskService.getTaskById(id);
            return ResponseEntity.ok(taskResponse);
        }
        catch (TaskNotFoundException e) {
            return handleException(e);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Getting all tasks, page: {}, size: {}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskResponseDTO> allTasks = taskService.getAllTasks(pageable);
            return ResponseEntity.ok(allTasks.getContent());
        }
        catch (UnauthorizedAccessException e) {
            return handleException(e);
        }
    }

    @GetMapping("/created")
    public ResponseEntity<?> getAllCreatedTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all created tasks, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponseDTO> allCreatedTasks = taskService.getAllCreatedTasks(pageable);
        return ResponseEntity.ok(allCreatedTasks.getContent());
    }

    @GetMapping("/assigned")
    public ResponseEntity<?> getAllAssignedTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all assigned tasks, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponseDTO> allAssignedTasks = taskService.getAllAssignedTasks(pageable);
        return ResponseEntity.ok(allAssignedTasks.getContent());
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<?> getAllAssignedTasksForUser(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Getting all assigned tasks for user: {}, page: {}, size: {}", userId, page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskResponseDTO> allAssignedTasksForUser = taskService.getAllAssignedTasksForUser(userId, pageable);
            return ResponseEntity.ok(allAssignedTasksForUser.getContent());
        }
        catch (UnauthorizedAccessException e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable Long id) {
        try {
            log.info("Deleting task: {}", id);
            taskService.deleteTaskById(id);
            return ResponseEntity.noContent().build();
        }
        catch (UnauthorizedAccessException | TaskNotFoundException e) {
            return handleException(e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDTO>> searchAndFilterTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, name = "desc") String description,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false, name = "from") LocalDate fromDueDate,
            @RequestParam(required = false, name = "to") LocalDate toDueDate
    ) {
        TaskSearchCriteriaDTO taskSearchCriteriaDTO = TaskSearchCriteriaDTO.builder()
                .title(title)
                .description(description)
                .status(status)
                .priority(priority)
                .fromDueDate(fromDueDate)
                .toDueDate(toDueDate)
                .build();
        List<TaskResponseDTO> filteredTasks = taskService.searchAndFilterTasks(taskSearchCriteriaDTO);
        return ResponseEntity.ok(filteredTasks);
    }

    @GetMapping("{taskId}/history")
    public ResponseEntity<List<TaskHistoryResponseDTO>> getTaskHistory(@PathVariable Long taskId) {
        log.info("Getting history for task: {}", taskId);
        List<TaskHistoryResponseDTO> history = taskHistoryService.getTaskHistory(taskId);
        return ResponseEntity.ok(history);
    }

    private ResponseEntity<?> handleException(Exception e) {
        if (e instanceof UnauthorizedAccessException) {
            log.error("Unauthorized access: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to access this!");
        } else if (e instanceof TaskNotFoundException) {
            log.error("Task not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
        else if (e instanceof HttpClientErrorException.BadRequest) {
            log.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        else {
            log.error("Internal server error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}


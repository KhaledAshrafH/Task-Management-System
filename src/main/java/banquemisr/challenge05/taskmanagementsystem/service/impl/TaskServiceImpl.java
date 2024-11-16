package banquemisr.challenge05.taskmanagementsystem.service.impl;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.*;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.Task;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.ActionType;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.NotificationType;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.UserRole;
import banquemisr.challenge05.taskmanagementsystem.domain.mapper.TaskMapper;
import banquemisr.challenge05.taskmanagementsystem.exception.TaskNotFoundException;
import banquemisr.challenge05.taskmanagementsystem.exception.UnauthorizedAccessException;
import banquemisr.challenge05.taskmanagementsystem.repository.TaskRepository;
import banquemisr.challenge05.taskmanagementsystem.repository.UserRepository;
import banquemisr.challenge05.taskmanagementsystem.service.NotificationService;
import banquemisr.challenge05.taskmanagementsystem.service.TaskHistoryService;
import banquemisr.challenge05.taskmanagementsystem.service.TaskService;
import banquemisr.challenge05.taskmanagementsystem.util.UtilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final TaskHistoryService taskHistoryService;
    private final NotificationService notificationService;

    @Override
    public TaskResponseDTO createTask(TaskCreationDTO taskCreationDTO) {
        User currentUser = UtilityService.getCurrentUser();
        Task task = taskMapper.toEntity(taskCreationDTO);
        task.setStatus(taskCreationDTO.getStatus() != null ? taskCreationDTO.getStatus() : TaskStatus.TODO);
        task.setCreatedBy(currentUser);
        task.setAssignedTo(currentUser);

        Task savedTask = taskRepository.save(task);

        logTaskHistory(savedTask.getId(), ActionType.CREATED, null, savedTask.getStatus(), null);

        String message = "A new task \"" + savedTask.getTitle() + "\" has been created.";
        sendNotification(currentUser.getId(), message, NotificationType.TASK_CREATED);

        return taskMapper.toResponseDTO(savedTask);
    }


    @Override
    public TaskResponseDTO assignTask(TaskCreationDTO taskCreationDTO) {
        if (!hasPermissionToAssign())
            throw new UnauthorizedAccessException("You are not allowed to assign tasks.");

        User assignedUser = userRepository.findById(taskCreationDTO.getAssignedUserId()).orElseThrow(() -> new NoSuchElementException("User not found"));
        Task task = taskMapper.toEntity(taskCreationDTO);
        task.setStatus(TaskStatus.TODO);
        task.setCreatedBy(UtilityService.getCurrentUser());
        task.setAssignedTo(assignedUser);

        Task savedTask = taskRepository.save(task);

        logTaskHistory(savedTask.getId(), ActionType.CREATED, null, savedTask.getStatus(), null);

        String message = "You have been assigned a new task: \"" + savedTask.getTitle() + "\".";
        sendNotification(assignedUser.getId(), message, NotificationType.TASK_ASSIGNED);

        return taskMapper.toResponseDTO(savedTask);

    }

    @Override
    public TaskResponseDTO updateTask(Long taskId, @Valid TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findByIdAndDeletedAtNull(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!hasPermissionToUpdate(task))
            throw new UnauthorizedAccessException("You are not allowed to update this task.");

        TaskStatus oldStatus = task.getStatus();
        updateTaskProperties(task, taskUpdateDTO);
        Task savedTask = taskRepository.save(task);

        logTaskHistory(savedTask.getId(), ActionType.UPDATED, oldStatus, savedTask.getStatus(),
                taskUpdateDTO.getDescription() != null ? taskUpdateDTO.getDescription() : "");

        if (savedTask.getStatus() == TaskStatus.DONE) {
            String message = "Good work! Task \"" + savedTask.getTitle() + "\" has been completed!";
            sendNotification(savedTask.getId(), message, NotificationType.TASK_COMPLETED);
        }

        return taskMapper.toResponseDTO(savedTask);

    }

    private void updateTaskProperties(Task task, TaskUpdateDTO taskUpdateDTO) {
        if (taskUpdateDTO.getTitle() != null)
            task.setTitle(taskUpdateDTO.getTitle());

        if (taskUpdateDTO.getDescription() != null)
            task.setDescription(taskUpdateDTO.getDescription());

        if (taskUpdateDTO.getStatus() != null)
            task.setStatus(taskUpdateDTO.getStatus());

        if (taskUpdateDTO.getPriority() != null)
            task.setPriority(taskUpdateDTO.getPriority());

        if (taskUpdateDTO.getDueDate() != null)
            task.setDueDate(taskUpdateDTO.getDueDate());
    }


    @Override
    public TaskResponseDTO getTaskById(Long id) {
        User currentUser = UtilityService.getCurrentUser();

        Task task = taskRepository.findByIdAndDeletedAtNull(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (currentUser.getRole() == UserRole.ADMIN)
            return taskMapper.toResponseDTO(task);

        else {
            if (currentUser.getId().equals(task.getAssignedTo().getId()))
                return taskMapper.toResponseDTO(task);

            else
                throw new UnauthorizedAccessException("You are not allowed to assign this task.");
        }
    }

    @Override
    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
        User currentUser = UtilityService.getCurrentUser();
        if (currentUser.getRole() == UserRole.ADMIN) {
            Page<Task> tasks = taskRepository.findAllByDeletedAtIsNull(pageable);
            return tasks.map(taskMapper::toResponseDTO);
        }
        else
            throw new UnauthorizedAccessException("You are not allowed to get all tasks!");
    }

    @Override
    public Page<TaskResponseDTO> getAllCreatedTasks(Pageable pageable) {
        User currentUser = UtilityService.getCurrentUser();
        Page<Task> tasks = taskRepository.findAllByCreatedByAndDeletedAtNull(currentUser, pageable);
        return tasks.map(taskMapper::toResponseDTO);
    }

    @Override
    public Page<TaskResponseDTO> getAllAssignedTasks(Pageable pageable) {
        User currentUser = UtilityService.getCurrentUser();
        Page<Task> tasks = taskRepository.findAllByAssignedToAndDeletedAtNull(currentUser, pageable);
        return tasks.map(taskMapper::toResponseDTO);
    }

    @Override
    public Page<TaskResponseDTO> getAllAssignedTasksForUser(Long id, Pageable pageable) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        User currentUser = UtilityService.getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN ||
                (currentUser.getRole() == UserRole.USER && currentUser.getId().equals(id))) {
            Page<Task> tasks = taskRepository.findAllByAssignedToAndDeletedAtNull(user, pageable);
            return tasks.map(taskMapper::toResponseDTO);
        }
        else
            throw new RuntimeException("You are not allowed to assign these tasks.");

    }

    @Override
    public void deleteTaskById(Long id) {
        User currentUser = UtilityService.getCurrentUser();
        Task task = taskRepository.findByIdAndDeletedAtNull(id).orElseThrow(() -> new RuntimeException("Task not found"));

        if (hasPermissionToUpdate(task,currentUser)) {
            task.setDeletedAt(LocalDateTime.now());
            taskRepository.save(task);
            logTaskHistory(task.getId(), ActionType.DELETED, task.getStatus(), null, null);
        }
        else
            throw new RuntimeException("You are not allowed to delete this task");
    }

    @Override
    public List<TaskResponseDTO> searchAndFilterTasks(TaskSearchCriteriaDTO taskSearchCriteriaDTO) {
        User currentUser = UtilityService.getCurrentUser();
        Specification<Task> spec = Specification.where(createTaskCriteria(taskSearchCriteriaDTO));

        if (!currentUser.getRole().equals(UserRole.ADMIN))
            spec = spec.and((root, query, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("assignedTo").get("id"), currentUser.getId()));

        List<Task> tasks = taskRepository.findAll(spec);
        return taskMapper.toResponseDTOs(tasks);
    }


    @Scheduled(fixedRate = 1200000) // every 20 minutes for testing
    protected void notifyTasksDueSoon() {
        System.out.println("---------------------------------------------------Checking for due tasks: " + LocalDateTime.now());
        LocalDate now = LocalDate.now();
        LocalDate soonDue = now.plusDays(1);

        List<Task> tasksDueSoon = taskRepository.findByDueDateBetweenAndStatusNot(now, soonDue, TaskStatus.DONE);

        for (Task task : tasksDueSoon) {
            String message = "Reminder: The task \"" + task.getTitle() + "\" is due tomorrow!";
            sendNotification(task.getAssignedTo().getId(),message,NotificationType.TASK_DUE_SOON);
        }
    }

    private void logTaskHistory(Long taskId, ActionType actionType, TaskStatus oldStatus, TaskStatus newStatus, String changedDescription) {
        TaskHistoryLogDTO taskHistoryLogDTO = TaskHistoryLogDTO.builder()
                .taskId(taskId)
                .actionType(actionType)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedDescription(changedDescription)
                .build();
        taskHistoryService.logTaskHistory(taskHistoryLogDTO);
    }

    private boolean hasPermissionToAssign() {
        return UtilityService.getCurrentUser().getRole() == UserRole.ADMIN;
    }

    private boolean hasPermissionToUpdate(Task task) {
        User currentUser = UtilityService.getCurrentUser();
        return currentUser.getRole() == UserRole.ADMIN
                || (currentUser.getRole() == UserRole.USER && currentUser.getId().equals(task.getAssignedTo().getId()));
    }

    private boolean hasPermissionToUpdate(Task task,User currentUser) {
        return currentUser.getRole() == UserRole.ADMIN
                || (currentUser.getRole() == UserRole.USER && currentUser.getId().equals(task.getAssignedTo().getId()));
    }

    private Specification<Task> createTaskCriteria(TaskSearchCriteriaDTO taskSearchCriteriaDTO) {
        Specification<Task> spec = Specification.where(null);

        if (taskSearchCriteriaDTO.getTitle() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + taskSearchCriteriaDTO.getTitle() + "%"));
        }

        if (taskSearchCriteriaDTO.getDescription() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("description"), "%" + taskSearchCriteriaDTO.getDescription() + "%"));
        }

        if (taskSearchCriteriaDTO.getStatus() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), taskSearchCriteriaDTO.getStatus()));
        }

        if (taskSearchCriteriaDTO.getPriority() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("priority"), taskSearchCriteriaDTO.getPriority()));
        }

        if (taskSearchCriteriaDTO.getFromDueDate() != null || taskSearchCriteriaDTO.getToDueDate() != null) {
            LocalDate fromDueDate = taskSearchCriteriaDTO.getFromDueDate();
            LocalDate toDueDate = taskSearchCriteriaDTO.getToDueDate();
            spec = spec.and((root, query, criteriaBuilder) -> {
                if (fromDueDate != null && toDueDate != null)
                    return criteriaBuilder.between(root.get("dueDate"), fromDueDate, toDueDate);

                else if (fromDueDate != null)
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), fromDueDate);

                else
                    return criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), toDueDate);
            });
        }

        return spec;
    }

    private void sendNotification(Long id, String message, NotificationType notificationType) {
        NotificationCreationDTO notification = NotificationCreationDTO.builder()
                .userId(id)
                .message(message)
                .type(notificationType)
                .build();
        notificationService.sendNotification(notification);
    }
}

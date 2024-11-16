package banquemisr.challenge05.taskmanagementsystem.service;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.Task;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskPriority;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.UserRole;
import banquemisr.challenge05.taskmanagementsystem.domain.mapper.TaskMapper;
import banquemisr.challenge05.taskmanagementsystem.exception.UnauthorizedAccessException;
import banquemisr.challenge05.taskmanagementsystem.repository.TaskRepository;
import banquemisr.challenge05.taskmanagementsystem.repository.UserRepository;
import banquemisr.challenge05.taskmanagementsystem.service.impl.TaskServiceImpl;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceImplTest {
    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskHistoryService taskHistoryService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TaskMapper taskMapper;

    private User mockUser;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser  = new User();
        mockUser .setId(1L);
        mockUser .setUsername("testUser ");
        mockUser .setRole(UserRole.ADMIN);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser , null, mockUser .getAuthorities())
        );

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.LOW);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setCreatedBy(mockUser);
        task.setAssignedTo(mockUser);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createTask_ShouldCreateTask() {
        TaskCreationDTO taskCreationDTO = new TaskCreationDTO();
        taskCreationDTO.setTitle("New Task");
        taskCreationDTO.setDescription("New Task Description");
        taskCreationDTO.setStatus(TaskStatus.TODO);
        taskCreationDTO.setPriority(TaskPriority.LOW);
        taskCreationDTO.setDueDate(LocalDate.now().plusDays(1));

        when(taskMapper.toEntity(any())).thenReturn(task);
        when(taskRepository.save(any())).thenReturn(task);
        when(taskMapper.toResponseDTO(any())).thenReturn(new TaskResponseDTO());

        TaskResponseDTO response = taskService.createTask(taskCreationDTO);

        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
        verify(notificationService).sendNotification(any());
    }


    @Test
    void createTask_NullTitle_ShouldThrowException() {
        TaskCreationDTO taskCreationDTO = new TaskCreationDTO();
        taskCreationDTO.setDescription("Valid Description");
        taskCreationDTO.setStatus(TaskStatus.TODO);
        taskCreationDTO.setPriority(TaskPriority.LOW);
        taskCreationDTO.setDueDate(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> taskService.createTask(taskCreationDTO));
    }

    @Test
    void createTask_NullDescription_ShouldThrowException() {
        TaskCreationDTO taskCreationDTO = new TaskCreationDTO();
        taskCreationDTO.setTitle("Valid Title");
        taskCreationDTO.setStatus(TaskStatus.TODO);
        taskCreationDTO.setPriority(TaskPriority.LOW);
        taskCreationDTO.setDueDate(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> taskService.createTask(taskCreationDTO));
    }

    @Test
    void createTask_DueDateInPast_ShouldThrowException() {
        TaskCreationDTO taskCreationDTO = new TaskCreationDTO();
        taskCreationDTO.setTitle("Valid Title");
        taskCreationDTO.setDescription("Valid Description");
        taskCreationDTO.setStatus(TaskStatus.TODO);
        taskCreationDTO.setPriority(TaskPriority.LOW);
        taskCreationDTO.setDueDate(LocalDate.now().minusDays(3)); // Past date

        assertThrows(Exception.class, () -> taskService.createTask(taskCreationDTO));
    }


    @Test
    void createTask_MapperReturnsNull_ShouldThrowException() {
        TaskCreationDTO taskCreationDTO = new TaskCreationDTO();
        taskCreationDTO.setTitle("Valid Title");
        taskCreationDTO.setDescription("Valid Description");
        taskCreationDTO.setStatus(TaskStatus.TODO);
        taskCreationDTO.setPriority(TaskPriority.LOW);
        taskCreationDTO.setDueDate(LocalDate.now().plusDays(1));

        when(taskMapper.toEntity(any())).thenReturn(null); // Simulate mapper returning null

        assertThrows(Exception.class, () -> taskService.createTask(taskCreationDTO));
    }


    @Test
    void assignTask_WithoutPermission_ShouldThrowUnauthorizedAccessException() {
        // Set up a user without permission
        User mockUser  = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setFirstName("Test");
        mockUser.setLastName("Test");
        mockUser.setRole(UserRole.USER); // Assuming USER role does not have permission

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser , null, mockUser.getAuthorities())
        );

        TaskCreationDTO taskCreationDTO = new TaskCreationDTO();
        taskCreationDTO.setTitle("Valid Title");
        taskCreationDTO.setDescription("Valid Description");
        taskCreationDTO.setStatus(TaskStatus.TODO);
        taskCreationDTO.setPriority(TaskPriority.LOW);
        taskCreationDTO.setDueDate(LocalDate.now().plusDays(1));

        assertThrows(UnauthorizedAccessException.class, () -> taskService.assignTask(taskCreationDTO));
    }

    @Test
    void assignTask_ShouldAssignTask() {
        TaskCreationDTO taskCreationDTO = new TaskCreationDTO();
        taskCreationDTO.setAssignedUserId(2L);
        taskCreationDTO.setTitle("Assigned Task");

        User assignedUser  = new User();
        assignedUser.setId(2L);
        assignedUser.setRole(UserRole.ADMIN);

        when(userRepository.findById(2L)).thenReturn(Optional.of(assignedUser ));
        when(taskMapper.toEntity(any())).thenReturn(task);
        when(taskRepository.save(any())).thenReturn(task);
        when(taskMapper.toResponseDTO(any())).thenReturn(new TaskResponseDTO());

        TaskResponseDTO response = taskService.assignTask(taskCreationDTO);

        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
        verify(notificationService).sendNotification(any());
    }

}

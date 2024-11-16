package banquemisr.challenge05.taskmanagementsystem.controller;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.TaskUpdateDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskHistoryResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.ActionType;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskPriority;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import banquemisr.challenge05.taskmanagementsystem.service.TaskHistoryService;
import banquemisr.challenge05.taskmanagementsystem.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerTest {
    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskHistoryService taskHistoryService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        TaskCreationDTO taskCreationDTO = TaskCreationDTO.builder()
                .title("Test Title")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(2))
                .priority(TaskPriority.HIGH)
                .build();

        TaskResponseDTO taskResponseDTO = TaskResponseDTO.builder()
                .title("Test Title")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(2))
                .priority(TaskPriority.HIGH)
                .build();

        when(taskService.createTask(any(TaskCreationDTO.class))).thenReturn(taskResponseDTO);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreationDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(taskResponseDTO.getTitle()));

        verify(taskService, times(1)).createTask(any(TaskCreationDTO.class));
    }

    @Test
    void assignTask_ShouldReturnCreatedTask_WhenAdmin() throws Exception {
        TaskCreationDTO taskCreationDTO = TaskCreationDTO.builder()
                .title("Test Title")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(2))
                .priority(TaskPriority.HIGH)
                .build();

        TaskResponseDTO taskResponseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(2))
                .priority(TaskPriority.HIGH)
                .build();

        when(taskService.assignTask(any(TaskCreationDTO.class))).thenReturn(taskResponseDTO);

        mockMvc.perform(post("/api/v1/tasks/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreationDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(taskResponseDTO.getTitle()));

        verify(taskService, times(1)).assignTask(any(TaskCreationDTO.class));
    }


    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        Long taskId = 1L;
        TaskUpdateDTO taskUpdateDTO = TaskUpdateDTO.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDate.now().plusDays(3))
                .priority(TaskPriority.MEDIUM)
                .build();

        TaskResponseDTO taskResponseDTO = TaskResponseDTO.builder()
                .id(taskId)
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDate.now().plusDays(3))
                .priority(TaskPriority.MEDIUM)
                .build();

        when(taskService.updateTask(eq(taskId), any(TaskUpdateDTO.class))).thenReturn(taskResponseDTO);

        mockMvc.perform(put("/api/v1/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(taskResponseDTO.getTitle()));

        verify(taskService, times(1)).updateTask(eq(taskId), any(TaskUpdateDTO.class));
    }

    @Test
    void deleteTask_ShouldReturnNoContent() throws Exception {
        Long taskId = 1L;

        doNothing().when(taskService).deleteTaskById(taskId);

        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTaskById(taskId);
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        Long taskId = 1L;
        TaskResponseDTO taskResponseDTO = TaskResponseDTO.builder()
                .id(taskId)
                .title("Test Title")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(2))
                .priority(TaskPriority.HIGH)
                .build();

        when(taskService.getTaskById(taskId)).thenReturn(taskResponseDTO);

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(taskResponseDTO.getTitle()));

        verify(taskService, times(1)).getTaskById(taskId);
    }

    @Test
    void getTaskHistory_ShouldReturnTaskHistory() throws Exception {
        Long taskId = 1L;
        TaskHistoryResponseDTO taskHistoryResponseDTO = new TaskHistoryResponseDTO();
        taskHistoryResponseDTO.setTaskId(taskId);
        taskHistoryResponseDTO.setActionType(ActionType.CREATED);

        when(taskHistoryService.getTaskHistory(taskId)).thenReturn(Collections.singletonList(taskHistoryResponseDTO));

        mockMvc.perform(get("/api/v1/tasks/{id}/history", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(taskHistoryService, times(1)).getTaskHistory(taskId);
    }
}

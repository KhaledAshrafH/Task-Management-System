package banquemisr.challenge05.taskmanagementsystem.domain.dto.request;

import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskPriority;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreationDTO {
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 1024, message = "Description cannot exceed 1024 characters")
    private String description;

    private TaskStatus status;

    @NotNull(message = "Priority cannot be null")
    private TaskPriority priority;

    @NotNull(message = "Due date cannot be null")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    private Long assignedUserId;
}

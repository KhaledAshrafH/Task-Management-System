package banquemisr.challenge05.taskmanagementsystem.domain.dto.request;

import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskPriority;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSearchCriteriaDTO {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate fromDueDate;
    private LocalDate toDueDate;
}

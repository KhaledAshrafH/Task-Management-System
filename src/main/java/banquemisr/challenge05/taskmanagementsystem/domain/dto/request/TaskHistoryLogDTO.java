package banquemisr.challenge05.taskmanagementsystem.domain.dto.request;

import banquemisr.challenge05.taskmanagementsystem.domain.enums.ActionType;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistoryLogDTO {
    private Long taskId;
    private String changedDescription;
    private ActionType actionType;
    private TaskStatus oldStatus;
    private TaskStatus newStatus;
}

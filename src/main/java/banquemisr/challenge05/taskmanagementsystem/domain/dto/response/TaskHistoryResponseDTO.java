package banquemisr.challenge05.taskmanagementsystem.domain.dto.response;

import banquemisr.challenge05.taskmanagementsystem.domain.enums.ActionType;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistoryResponseDTO {
    private Long id;
    private TaskStatus oldStatus;
    private TaskStatus newStatus;
    private String changedDescription;
    private ActionType actionType;
    private Long taskId;
    private Long changedByUserId;
    private LocalDateTime changedAt;
}

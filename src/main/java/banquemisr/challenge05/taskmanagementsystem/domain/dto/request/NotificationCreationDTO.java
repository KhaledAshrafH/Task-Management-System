package banquemisr.challenge05.taskmanagementsystem.domain.dto.request;

import banquemisr.challenge05.taskmanagementsystem.domain.enums.NotificationStatus;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.NotificationType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreationDTO {
    private Long userId;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
}


package banquemisr.challenge05.taskmanagementsystem.domain.dto.response;

import banquemisr.challenge05.taskmanagementsystem.domain.enums.NotificationStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long notificationId;
    private String message;
    private NotificationStatus status;
}

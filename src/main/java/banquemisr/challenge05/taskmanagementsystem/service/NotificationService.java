package banquemisr.challenge05.taskmanagementsystem.service;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.NotificationCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.NotificationResponseDTO;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public interface NotificationService {
    void sendNotification(NotificationCreationDTO notification);
    List<NotificationResponseDTO> getNotificationsForCurrentUser();
    List<NotificationResponseDTO> getNotificationsForSpecificUser(Long userId);
    void markNotificationAsRead(Long notificationId);
    void deleteNotification(Long notificationId);
}

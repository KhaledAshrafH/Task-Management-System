package banquemisr.challenge05.taskmanagementsystem.controller;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.NotificationResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskHistoryResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.UserResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.service.NotificationService;
import banquemisr.challenge05.taskmanagementsystem.service.TaskHistoryService;
import banquemisr.challenge05.taskmanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RequestMapping("api/v1")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final TaskHistoryService taskHistoryService;
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        log.info("Fetched all users successfully");
        return ResponseEntity.ok(users);
    }

    @GetMapping("users/me/history")
    public ResponseEntity<List<TaskHistoryResponseDTO>> getUserTaskHistory() {
        List<TaskHistoryResponseDTO> userTaskHistory = taskHistoryService.getUserTaskHistory();
        log.info("Fetched task history for current user successfully");
        return ResponseEntity.ok(userTaskHistory);
    }

    @GetMapping("users/me/notifications")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotificationsForUser () {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsForCurrentUser ();
        log.info("Fetched notifications for current user successfully");
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("users/{userId}/notifications")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotificationsForUser (@PathVariable Long userId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsForSpecificUser (userId);
        log.info("Fetched notifications for user id: {} successfully", userId);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("users/me/notifications/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        log.info("Deleted notification id: {} successfully", notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("users/me/notifications/{notificationId}")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        log.info("Marked notification id: {} as read successfully", notificationId);
        return ResponseEntity.ok("The notification has been marked as read");
    }
}

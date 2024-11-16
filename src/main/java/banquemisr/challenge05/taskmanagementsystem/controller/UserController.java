package banquemisr.challenge05.taskmanagementsystem.controller;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.NotificationResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.TaskHistoryResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.UserResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.exception.UnauthorizedAccessException;
import banquemisr.challenge05.taskmanagementsystem.service.NotificationService;
import banquemisr.challenge05.taskmanagementsystem.service.TaskHistoryService;
import banquemisr.challenge05.taskmanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            log.info("Fetched all users successfully");
            return ResponseEntity.status(HttpStatus.OK).body(users);
        }
        catch (UnauthorizedAccessException e) {
            log.warn("Unauthorized access attempted to fetch all users");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (Exception e) {
            log.error("Error fetching all users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching users");
        }
    }

    @GetMapping("users/me/history")
    ResponseEntity<?> getUserTaskHistory() {
        try {
            List<TaskHistoryResponseDTO> userTaskHistory = taskHistoryService.getUserTaskHistory();
            log.info("Fetched task history for current user successfully");
            return ResponseEntity.status(HttpStatus.OK).body(userTaskHistory);
        }
        catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("users/me/notifications")
    public ResponseEntity<?> getAllNotificationsForUser() {
        try {
            List<NotificationResponseDTO> notifications = notificationService.getNotificationsForCurrentUser();
            log.info("Fetched notifications for current user successfully");
            return ResponseEntity.ok(notifications);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching notifications");
        }
    }

    @GetMapping("users/{userId}/notifications")
    public ResponseEntity<?> getAllNotificationsForUser(@PathVariable Long userId) {
        try {
            List<NotificationResponseDTO> notifications = notificationService.getNotificationsForSpecificUser(userId);
            log.info("Fetched notifications for user id: {} successfully", userId);
            return ResponseEntity.ok(notifications);
        }
        catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("users/me/notifications/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        try{
            notificationService.deleteNotification(notificationId);
            log.info("Deleted notification id: {} successfully", notificationId);
            return ResponseEntity.noContent().build();
        }
        catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("users/me/notifications/{notificationId}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markNotificationAsRead(notificationId);
            log.info("Marked notification id: {} as read successfully", notificationId);
            return ResponseEntity.ok("The notification has been marked as read");
        }
        catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

package banquemisr.challenge05.taskmanagementsystem.service.impl;

import banquemisr.challenge05.taskmanagementsystem.domain.dto.request.NotificationCreationDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.dto.response.NotificationResponseDTO;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.Notification;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.NotificationStatus;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.UserRole;
import banquemisr.challenge05.taskmanagementsystem.domain.mapper.NotificationMapper;
import banquemisr.challenge05.taskmanagementsystem.exception.UnauthorizedAccessException;
import banquemisr.challenge05.taskmanagementsystem.repository.NotificationRepository;
import banquemisr.challenge05.taskmanagementsystem.repository.UserRepository;
import banquemisr.challenge05.taskmanagementsystem.service.NotificationService;
import banquemisr.challenge05.taskmanagementsystem.util.UtilityService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Override
    public void sendNotification(NotificationCreationDTO notificationCreationDTO) {
        Notification notification = createNotification(notificationCreationDTO);
        Notification savedNotification = notificationRepository.save(notification);
        sendEmailNotification(savedNotification);

        notificationMapper.toResponseDTO(savedNotification);
    }

    @Override
    public List<NotificationResponseDTO> getNotificationsForCurrentUser() {
        User user = UtilityService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findByUserAndStatusNot(user,NotificationStatus.DELETED);
        return notificationMapper.toResponseDTOs(notifications);
    }

    @Override
    public List<NotificationResponseDTO> getNotificationsForSpecificUser(Long userId) {
        userRepository.findById(userId).orElseThrow(()-> new NoSuchElementException("User not found"));
        User currentUser = UtilityService.getCurrentUser();
        if (currentUser.getRole() == UserRole.ADMIN){
            List<Notification> notifications = notificationRepository.findByUserAndStatusNot(currentUser,NotificationStatus.DELETED);
            return notificationMapper.toResponseDTOs(notifications);
        }
        else {
            if (currentUser.getId().equals(userId)){
                List<Notification> notifications = notificationRepository.findByUserAndStatusNot(currentUser,NotificationStatus.DELETED);
                return notificationMapper.toResponseDTOs(notifications);
            }
            else
                throw new UnauthorizedAccessException("You are not allowed to assign this task.");
        }
    }

    private void sendEmailNotification(Notification notification) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setContent(notification.getMessage(), "text/html");
            message.setSubject("Task Management System Notification");
            message.setFrom("hotelhuborg@gmail.com");
//            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(notification.getUser().getEmail()));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("5aleda4rf@gmail.com")); // for test only
            mailSender.send(message);
        }
        catch (MessagingException e) {
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        User currentUser = UtilityService.getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if(isAuthorizedToUpdate(notification, currentUser)) {
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
        }
        else
            throw new RuntimeException("You are not allowed to mark this notification");
    }

    @Override
    public void deleteNotification(Long notificationId) {
        User currentUser = UtilityService.getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if(isAuthorizedToUpdate(notification, currentUser)) {
            notification.setStatus(NotificationStatus.DELETED);
            notificationRepository.save(notification);
        }
        else
            throw new UnauthorizedAccessException("You are not allowed to mark this notification");
    }

    private Notification createNotification(NotificationCreationDTO notificationCreationDTO) {
        User user = userRepository.findById(notificationCreationDTO.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Notification notification = notificationMapper.toEntity(notificationCreationDTO);
        notification.setUser(user);
        notification.setStatus(NotificationStatus.UNREAD);
        return notification;
    }

    private boolean isAuthorizedToUpdate(Notification notification, User user) {
        return user.getId().equals(notification.getUser().getId());
    }
}

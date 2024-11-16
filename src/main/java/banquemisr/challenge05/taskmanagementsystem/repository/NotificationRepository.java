package banquemisr.challenge05.taskmanagementsystem.repository;

import banquemisr.challenge05.taskmanagementsystem.domain.entity.Notification;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndStatusNot(User user, NotificationStatus notificationStatus);
    List<Notification> findByUserAndStatus(User user, NotificationStatus notificationStatus);
}

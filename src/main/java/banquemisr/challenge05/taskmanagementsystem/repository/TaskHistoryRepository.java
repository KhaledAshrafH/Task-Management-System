package banquemisr.challenge05.taskmanagementsystem.repository;

import banquemisr.challenge05.taskmanagementsystem.domain.entity.Task;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.TaskHistory;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findAllByTask(Task task);
    List<TaskHistory> findAllByChangedBy(User user);
}

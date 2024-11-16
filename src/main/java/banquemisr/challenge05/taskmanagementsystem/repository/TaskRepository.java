package banquemisr.challenge05.taskmanagementsystem.repository;

import banquemisr.challenge05.taskmanagementsystem.domain.entity.Task;
import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> , JpaSpecificationExecutor<Task> {
    Page<Task> findAllByAssignedToAndDeletedAtNull(User user,Pageable pageable);
    Page<Task> findAllByCreatedByAndDeletedAtNull(User user,Pageable pageable);
    Page<Task> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Task> findByIdAndDeletedAtNull(Long id);
    List<Task> findByDueDateBetweenAndStatusNot(LocalDate start, LocalDate end, TaskStatus status);

}

package banquemisr.challenge05.taskmanagementsystem.domain.entity;

import banquemisr.challenge05.taskmanagementsystem.domain.enums.ActionType;
import banquemisr.challenge05.taskmanagementsystem.domain.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "old_status")
    @Enumerated(EnumType.STRING)
    private TaskStatus oldStatus;

    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    private TaskStatus newStatus;

    @Column(name = "changed_description")
    private String changedDescription;

    @Column(name = "action_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;

    @Override
    public String toString() {
        return "TaskHistory{" +
                "id=" + id +
                ", actionType=" + actionType +
                ", changedAt=" + changedAt +
                '}';
    }
}

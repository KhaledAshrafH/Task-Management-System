package banquemisr.challenge05.taskmanagementsystem.domain.dto.response;

import banquemisr.challenge05.taskmanagementsystem.domain.enums.UserRole;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    @Transient
    String getFullName() {
        return firstName + " " + lastName;
    }
}

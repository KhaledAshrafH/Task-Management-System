package banquemisr.challenge05.taskmanagementsystem.domain.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDTO {
    private String accessToken;
    private String username;
}

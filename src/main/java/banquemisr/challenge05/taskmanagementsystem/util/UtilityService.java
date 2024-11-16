package banquemisr.challenge05.taskmanagementsystem.util;

import banquemisr.challenge05.taskmanagementsystem.domain.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public interface UtilityService {
    static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

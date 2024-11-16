package banquemisr.challenge05.taskmanagementsystem.security;

import banquemisr.challenge05.taskmanagementsystem.domain.entity.Token;
import banquemisr.challenge05.taskmanagementsystem.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return;

        String jwt = authHeader.substring(7);

        Token storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);

        if (storedToken != null) {
            invalidateToken(storedToken);
            SecurityContextHolder.clearContext();
        }
    }

    private void invalidateToken(Token token) {
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
    }
}
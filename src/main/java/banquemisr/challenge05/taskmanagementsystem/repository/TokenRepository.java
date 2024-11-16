package banquemisr.challenge05.taskmanagementsystem.repository;

import banquemisr.challenge05.taskmanagementsystem.domain.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    @Query(value = "SELECT t FROM Token t " +
            "WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokensByUserId(Long userId);

    List<Token> findAllByUserIdAndExpiredFalseOrRevokedFalse(Long userId);
}

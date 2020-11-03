package pfa.ordodistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pfa.ordodistance.models.VerificationToken;

import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    List<VerificationToken> findByUserEmail(String email);
    List<VerificationToken> findByToken(String token);
}

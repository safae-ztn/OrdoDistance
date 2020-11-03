package pfa.ordodistance.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pfa.ordodistance.models.User;
import pfa.ordodistance.models.VerificationToken;
import pfa.ordodistance.repositories.UserRepository;
import pfa.ordodistance.repositories.VerificationTokenRepository;
import pfa.ordodistance.services.SendingVerificationMailService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationMailService {
    @Autowired private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private SendingVerificationMailService sendingVerificationMailService;

    public void createVerification(User user){
    	
        user.setPassword(encoder.encode(user.getPassword()));
        user.setIsVerified(false);
        VerificationToken verificationToken;
        verificationToken= new VerificationToken();
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        Boolean b=sendingVerificationMailService.sendVerificationMail(user, verificationToken.getToken());
    }

    public ResponseEntity<String> verifyEmail(String token){
    	
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByToken(token);
        if (verificationTokens.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid token.");
        }
        VerificationToken verificationToken = verificationTokens.get(0);
        if (verificationToken.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.unprocessableEntity().body("Expired token.");
        }
        verificationToken.setConfirmedDateTime(LocalDateTime.now());
        verificationToken.getUser().setIsVerified(true);
        verificationTokenRepository.save(verificationToken);
        return ResponseEntity.ok("You have successfully verified your email address.");
    }
}

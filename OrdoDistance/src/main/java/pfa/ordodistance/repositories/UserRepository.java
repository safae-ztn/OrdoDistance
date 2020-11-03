package pfa.ordodistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfa.ordodistance.models.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	User findByUsername(String username);
	List<User> findByEmail(String email);
	User findByPpt(String ppt);
}

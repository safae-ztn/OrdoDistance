package pfa.ordodistance.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pfa.ordodistance.models.User;
import pfa.ordodistance.models.UserPrincipal;
import pfa.ordodistance.repositories.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{
	 
	@Autowired UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String ppt) throws UsernameNotFoundException {
		System.out.println("pppppppppp"+ppt);
			User user = userRepository.findByPpt(ppt);
			if(user == null) {
				throw new UsernameNotFoundException("User not found!");
			}
			return new UserPrincipal(user);
	}
}

package pfa.ordodistance.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import pfa.ordodistance.models.User;
import pfa.ordodistance.services.UserService;
import pfa.ordodistance.services.VerificationMailService;

@Controller
@ComponentScan("pfa.ordodistance")
public class UserController {
	
	@Autowired 
	private UserService userService;

	@Autowired
	private VerificationMailService verificationMailService;
	
	@GetMapping("/users")
	public String getUsers() {
		return "User";
	}
	
	@PostMapping(value="users/addNew")
	public String addNew(User user/*, RedirectAttributes redir*/) {
		verificationMailService.createVerification(user);
		return "askVerification";
		/** Add it at the end of the code after clicking on the verification link
		RedirectView  redirectView= new RedirectView("/login",true);
	    redir.addFlashAttribute("message",	"You successfully registered! You can now login");
	    return redirectView;*/
	}

	@GetMapping("/verify-email")
	@ResponseBody
	public RedirectView verifyEmail(String token,RedirectAttributes redir) {
		verificationMailService.verifyEmail(token).getBody();
		RedirectView  redirectView= new RedirectView("/login",true);
		redir.addFlashAttribute("message",	"Votre authentification est confirmé!");
		return redirectView;
	}
}

package pfa.ordodistance;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {
	
	@GetMapping("/prescriptionPage")
	public String goHome() {
		return "prescriptionPage";
	}
	
	@GetMapping("/PatientPage")
	public String Patient() {
		return "PatientPage";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("/firstPage")
	public String first() {
		return "firstPage";
	}

	
	@GetMapping("/logout")
	public String logout() {
		return "login";
	}	
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	@GetMapping("/SendingWaysPage")
	public String goSend() {
		return "SendingWaysPage";
	}

}

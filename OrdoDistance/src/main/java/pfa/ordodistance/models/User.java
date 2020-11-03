package pfa.ordodistance.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
public class User {	
	
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
	private int id;
    private String firstname;
    private String lastname;
    private String ppt;
	private String username;
	private String email;
	private String password;
    private Boolean isVerified;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private VerificationToken verificationToken;
	
	@OneToMany(mappedBy=("user"))
	private List<Ordonnance> listOrdonnance;
	
	public User(int id, String firstname, String lastname, String username, String password,
			List<Ordonnance> listOrdonnance, String PPT) {
		super();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.password = password;
		this.ppt=PPT;
	}
	public User() {
		super();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPpt() {
		return this.ppt;
	}
	public void setPpt(String P) {
		this.ppt = P;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getIsVerified() {
		return isVerified;
	}
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getVerified() {
		return isVerified;
	}

	public void setVerified(Boolean verified) {
		isVerified = verified;
	}

	public VerificationToken getVerificationToken() {
		return verificationToken;
	}
	public void setVerificationToken(VerificationToken verificationToken) {
		this.verificationToken = verificationToken;
	}
}

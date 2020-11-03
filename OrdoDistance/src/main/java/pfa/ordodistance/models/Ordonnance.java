package pfa.ordodistance.models;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Ordonnance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private boolean isArchived=false;

	@Column(name="dateo")
	private Date dateo=new Date();
	
	@Column(name="maladie")
	private String maladie;
	
	@Column(name="isSent")
	private boolean isSent=false;

	
	@ManyToOne(fetch=FetchType.EAGER)
    private User user;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Patient patient;
	
	@OneToMany(mappedBy="idO")
	private List <Medicament> Medicaments;
	
	public Ordonnance() {
		super();
	}

	public Ordonnance(Date dateo, Integer medecinid,
			List<Medicament> medicaments) {
		super();
		this.dateo = dateo;
		Medicaments = medicaments;
	}

	public String getMaladie() {
		return maladie;
	}

	public void setMaladie(String maladie) {
		this.maladie = maladie;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Medicament> getMedicaments() {
		return Medicaments;
	}

	public void setMedicaments(List<Medicament> medicaments) {
		Medicaments = medicaments;
	}


	public boolean isArchived() {
		return isArchived;
	}

	public void setArchived(boolean archived) {
		isArchived = archived;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public Date getDateo() {
		return dateo;
	}
	public void setDateo(Date dateo) {
		this.dateo = dateo;
	}
	public Date getDateO() {
		return dateo;
	}
	public void setDateO(Date dateO) {
		this.dateo = dateO;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}

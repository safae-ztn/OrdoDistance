package pfa.ordodistance.models;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Patient {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="nom")
	private String nom;

	private Boolean isArchived=false;

	@Column(name="dateNaissance")
	@PastOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateNaissance;
	
	@Column(name="prenom")
	private String prenom;
	
	@Column(name="cin")
	private String CIN;
	
	public String getCIN() {
		return CIN;
	}
	public void setCIN(String cIN) {
		CIN = cIN;
	}
	@OneToMany(mappedBy="patient")
	private Set <Ordonnance> Ordonnances;
	
	public Patient() {
		super();
	}
	public Patient( String nomP, String prenom, LocalDate dateNaissance) {
		super();
		this.nom = nomP;
		this.prenom=prenom;
		this.dateNaissance = dateNaissance;
	}

	public Boolean getArchived() {
		return isArchived;
	}

	public void setArchived(Boolean archived) {
		isArchived = archived;
	}

	public String toString() {
		return "["+this.nom+" "+this.id+"]";
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nomP) {
		this.nom = nomP;
	}

	public LocalDate getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(LocalDate dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
	
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
}

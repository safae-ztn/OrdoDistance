package pfa.ordodistance.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Medicament {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="nom")
	private String nom;
	
	@Column(name="nbrefois")
	private String nbrefois;
	
	@Column(name="pendant")
	private String pendant;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Ordonnance idO;
	
	
	public Medicament() {
		super();
	}
	
	public Medicament(String nom, String nbrefois, String pendant,@NotNull Ordonnance ordonnance) {
		super();
		this.nom = nom;
		this.nbrefois = nbrefois;
		this.pendant = pendant;
		this.idO=ordonnance;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public Ordonnance getIdO() {
		return this.idO;
	}

	public void setIdO(Ordonnance idO) {
		this.idO = idO;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNbrefois() {
		return nbrefois;
	}

	public void setNbrefois(String nbrefois) {
		this.nbrefois = nbrefois;
	}

	public String getPendant() {
		return pendant;
	}

	public void setPendant(String pendant) {
		this.pendant = pendant;
	}

}

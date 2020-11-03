package pfa.ordodistance.dto;

import pfa.ordodistance.models.Medicament;
import java.util.List;

public class MedicamentsCreationDto {
	
	private List<Medicament> medicaments;
	 
    // default and parameterized constructor
	public MedicamentsCreationDto(List<Medicament> medicaments) {
		super();
		this.medicaments = medicaments;
	}
	public MedicamentsCreationDto() {
		super();
	}
    public void addBook(Medicament medicament) {
        this.medicaments.add(medicament);
    }
    
    // getter and setter
	public List<Medicament> getMedicaments() {
		return medicaments;
	}
	public void setMedicaments(List<Medicament> medicaments) {
		this.medicaments = medicaments;
	}
}

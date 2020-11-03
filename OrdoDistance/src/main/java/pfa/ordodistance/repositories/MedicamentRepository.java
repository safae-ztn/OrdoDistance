package pfa.ordodistance.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pfa.ordodistance.models.Ordonnance;

import pfa.ordodistance.models.Medicament;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Integer> {
 
	public List<Medicament> findByIdO(Ordonnance idO);
}

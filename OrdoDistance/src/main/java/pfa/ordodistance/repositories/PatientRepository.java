package pfa.ordodistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pfa.ordodistance.models.Patient;
import pfa.ordodistance.models.User;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer>{

    Patient findByCIN(String cin);
}

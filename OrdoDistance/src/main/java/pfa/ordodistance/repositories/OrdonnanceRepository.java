package pfa.ordodistance.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pfa.ordodistance.models.Ordonnance;
import pfa.ordodistance.models.Patient;
import pfa.ordodistance.models.User;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Integer> {

	List<Ordonnance> findByUser(User user);

	Ordonnance findById(int id);
	
	@Query(value="select * from ordonnance o where o.user_id=:user and "
			+ "(o.maladie like %:keyword% or"
			+ " o.dateo like %:keyword%)",nativeQuery=true)
	List <Ordonnance> findByUserAndKeyword(User user,@Param("keyword") String keyword);

	@Query(value="select * from patient p where p.id in (select patient_id from ordonnance o where o.user_id=:user and"
			+ " o.patient_id=p.id) and (p.cin like %:keyword% or "
			+ "p.nom like %:keyword% or p.prenom like %:keyword% or p.date_naissance like %:keyword% )"
			,nativeQuery=true)
	List<Integer> findByUserAndPatientKeyword(User user, @Param("keyword") String keyword);

	List<Ordonnance> findByUserAndPatient(User user, Patient byCin);

    List<Ordonnance> findByUserAndIsArchived(User user, boolean b);

	List<Ordonnance> findByUserAndIsSent(User user, boolean b);
}

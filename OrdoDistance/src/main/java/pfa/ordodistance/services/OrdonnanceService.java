package pfa.ordodistance.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pfa.ordodistance.models.Ordonnance;
import pfa.ordodistance.models.Patient;
import pfa.ordodistance.models.User;
import pfa.ordodistance.repositories.OrdonnanceRepository;
import pfa.ordodistance.repositories.PatientRepository;

@Service
public class OrdonnanceService {
	
	@Autowired(required = true)
	private OrdonnanceRepository ordonnanceRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
    //Save new ordonnance
	public void save(Ordonnance ord) {
		ordonnanceRepository.save(ord);
	}
	
	public List<Ordonnance> getOrdonnances(){
		return ordonnanceRepository.findAll();
	}
	
	public List<Ordonnance> getOrdonnancesByUser(User user){
		return ordonnanceRepository.findByUser(user);
	}
	
	public HashSet<Patient> getPatientsByUser(User user){
		HashSet<Patient> patients=new HashSet<Patient>();
		for(Ordonnance o:ordonnanceRepository.findByUser(user)) {
			patients.add(o.getPatient());
		}
		return patients;
	}
	
	public Ordonnance getOrdonnance(int id) {
		return ordonnanceRepository.findById(id);
	}
	
	//Get ordonnances by keyword
	public List<Ordonnance> getOrdonnancesByUserAndKeyword(User user,String keyword){
		return ordonnanceRepository.findByUserAndKeyword(user,keyword);
	}
	
	public List<Patient> getPatientsByUserAndKeyword(User user,String keyword){
		ArrayList<Patient> patients=new ArrayList<Patient>();
		for (int i : ordonnanceRepository.findByUserAndPatientKeyword(user,keyword)) {
			patients.add(patientRepository.findById(i).get());
		}
		return patients;
	}

	public void cancelOnCreation(User user){
		List<Ordonnance> ordos=ordonnanceRepository.findByUser(user);
		ordonnanceRepository.delete(ordos.get(ordos.size()-1));
	}

	public void sendOnCreation(User user){
		List<Ordonnance> ordos=ordonnanceRepository.findByUser(user);
		Ordonnance ordo=ordos.get(ordos.size()-1);
		ordo.setSent(true);
		ordonnanceRepository.save(ordo);
	}

	public List<Ordonnance> getOrdonnancesByPatient(User user,String cin){
		return ordonnanceRepository.findByUserAndPatient(user,patientRepository.findByCIN(cin));
	}

	public List<Ordonnance> getOrdonnancesByUserAndIsArchived(User user, boolean b) {
		return ordonnanceRepository.findByUserAndIsArchived(user,b);
	}

	public void delete(Ordonnance ordonnance) {
		ordonnanceRepository.delete(ordonnance);
	}

	public List<Ordonnance> getOrdonnancesByUserAndIsSent(User user, boolean b) {
		return ordonnanceRepository.findByUserAndIsSent(user,b);
	}
}

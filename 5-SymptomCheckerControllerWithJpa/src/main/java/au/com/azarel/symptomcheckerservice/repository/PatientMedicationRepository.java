package au.com.azarel.symptomcheckerservice.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * An interface for a repository that can store PatientMedication objects
 * objects and allow them to be searched by title.
 * 
 * @author jules
 *
 */

@Repository
public interface PatientMedicationRepository extends CrudRepository<PatientMedication, Long>{

	// Find all PatientMedications with a matching medication [name]
	public Collection<PatientMedication> findByMedication(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Patients
			@Param("medication") String medication);

	// Find all PatientMedications for a given patient
	public Collection<PatientMedication> findPatientMedicationByPmPatientId(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Videos
			@Param("pmPatientId") long pmPatientId);

	/*
	 * See: http://docs.spring.io/spring-data/jpa/docs/1.3.0.RELEASE/reference/html/jpa.repositories.html 
	 * for more examples of writing query methods
	 */
	
}

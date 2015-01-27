package au.com.azarel.symptomcheckerservice.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//import au.com.azarel.symptomcheckerservice.client.PatientSvcApi;

/**
 * An interface for a repository that can store Patient
 * objects and allow them to be searched for.
 * 
 * @author jules
 *
 */
//
@Repository
public interface PatientRepository extends CrudRepository<Patient, Long>{

	// Find all Patients with a matching last name in a list of patientId's
	@Query("from Patient p where UPPER(p.lastName) LIKE CONCAT(UPPER(TRIM(:lastName)), '%') and p.patientId in (:listIds) order by UPPER(p.lastName)")
	public Collection<Patient> findPatientByLastNameAndList(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Patients
			@Param("lastName") String lastName,
			@Param("listIds") List<Long> listIds);

	// Find all Patients with a matching userId
	public Collection<Patient> findPatientByUserId(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Patients
			@Param("userId") long userId);
	
	/*
	 * See: http://docs.spring.io/spring-data/jpa/docs/1.3.0.RELEASE/reference/html/jpa.repositories.html 
	 * for more examples of writing query methods
	 */
	
}

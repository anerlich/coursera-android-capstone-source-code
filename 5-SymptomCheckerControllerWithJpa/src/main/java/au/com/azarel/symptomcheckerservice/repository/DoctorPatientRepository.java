package au.com.azarel.symptomcheckerservice.repository;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * An interface for a repository that can store DoctorPatient link table
 * objects and allow them to be searched by description or doctorId.
 * 
 * @author jules
 *
 */


@Repository
public interface DoctorPatientRepository extends CrudRepository<DoctorPatient, Long>{

	// Find all DoctorPatients with a matching description
	public Collection<DoctorPatient> findByDescription(
			@Param("description") String description);

	// Find all DoctorPatients with a matching doctorId
	public Collection<DoctorPatient> findDoctorPatientByDpDoctorId(
			@Param("dpDoctorId") long dpDoctorId);
	/*	public Collection<DoctorPatient> findByPatientId(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Videos
			@Param("patientId") long patientId);
	 */
	/*
	 * See: http://docs.spring.io/spring-data/jpa/docs/1.3.0.RELEASE/reference/html/jpa.repositories.html 
	 * for more examples of writing query methods
	 */
	
}

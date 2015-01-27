package au.com.azarel.symptomcheckerservice.repository;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * An interface for a repository that can store CheckInMedication
 * objects and allow them to be searched by description or checkInId.
 * 
 * @author jules
 *
 */

@Repository
public interface CheckInMedicationRepository extends CrudRepository<CheckInMedication, Long>{

	// Find all patients with a matching title (e.g., Video.name)
	public Collection<CheckInMedication> findByDescription(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Videos
			@Param("description") String description);
	
	public Collection<CheckInMedication> findCheckInMedicationByCimCheckInId(
			@Param("cimCheckInId") long cimCheckInId);

	/*
	 * See: http://docs.spring.io/spring-data/jpa/docs/1.3.0.RELEASE/reference/html/jpa.repositories.html 
	 * for more examples of writing query methods
	 */
	
}

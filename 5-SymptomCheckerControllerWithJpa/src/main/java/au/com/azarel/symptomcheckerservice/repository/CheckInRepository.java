package au.com.azarel.symptomcheckerservice.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * An interface for a repository that can store Patient
 * objects and allow them to be searched by title.
 * 
 * @author jules
 *
 */
//
@Repository
public interface CheckInRepository extends CrudRepository<CheckIn, Long>{

	// Find all CheckIns with a matching description
	public Collection<CheckIn> findByDescription(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Videos
			@Param("description") String description);

	// Find CheckIns with a matching patientId which took place after a certain time
	@Query("from CheckIn c where c.ciPatientId = :ciPatientId and c.checkInTime >= :checkInTime order by c.checkInTime DESC")
	public Collection<CheckIn> findCheckInByCiPatientId(
			@Param("ciPatientId") long ciPatientId,
			@Param("checkInTime") long checkInTime);
	
	// Find CheckIns with a matching patientId which took place after a certain time
	// and a pain level in the array of supplied values
	@Query("from CheckIn c where c.ciPatientId = :ciPatientId and c.checkInTime >= :checkInTime and c.pain in (:pain) order by c.checkInTime DESC")
	public Collection<CheckIn> findCheckInByCiPatientIdPainType(
			@Param("ciPatientId") long ciPatientId,
			@Param("checkInTime") long checkInTime,
			@Param("pain") List<Pain> pain);

	// Find CheckIns with a matching patientId which took place after a certain time
	// and a eating affect level in the array of supplied values
	@Query("from CheckIn c where c.ciPatientId = :ciPatientId and c.checkInTime >= :checkInTime and c.eatingAffect in (:eatingAffect) order by c.checkInTime DESC")
	public Collection<CheckIn> findCheckInByCiPatientIdEatingAffectType(
			@Param("ciPatientId") long ciPatientId,
			@Param("checkInTime") long checkInTime,
			@Param("eatingAffect") List<EatingAffect> eatingAffect);
	/*
	 * See: http://docs.spring.io/spring-data/jpa/docs/1.3.0.RELEASE/reference/html/jpa.repositories.html 
	 * for more examples of writing query methods
	 */
	
}

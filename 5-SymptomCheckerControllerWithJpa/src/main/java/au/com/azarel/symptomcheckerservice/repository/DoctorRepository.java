package au.com.azarel.symptomcheckerservice.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * An interface for a repository that can store Doctor
 * objects and allow them to be searched by title.
 * 
 * @author jules
 *
 */
@Repository
public interface DoctorRepository extends CrudRepository<Doctor, Long>{

	// Find all Doctors with a matching last name
	public Collection<Doctor> findByLastName(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Patients
			@Param("lastName") String lastName);

	// Find Doctor matching a particular userId
	public Collection<Doctor> findDoctorByUserId(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Patients
			@Param("userId") long userId);
	
	/*
	 * See: http://docs.spring.io/spring-data/jpa/docs/1.3.0.RELEASE/reference/html/jpa.repositories.html 
	 * for more examples of writing query methods
	 */
	
}

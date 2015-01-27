package au.com.azarel.symptomcheckerservice.repository;

import java.util.Collection;

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
public interface UserRepository extends CrudRepository<User, Long>{

	// Find all Patients with a matching user name
	public Collection<User> findUserByUserName(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Users
			@Param("userName") String userName);
		
	/*
	 * See: http://docs.spring.io/spring-data/jpa/docs/1.3.0.RELEASE/reference/html/jpa.repositories.html 
	 * for more examples of writing query methods
	 */
	
}

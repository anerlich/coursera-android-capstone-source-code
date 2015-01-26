package au.com.azarel.symptomcheckerservice.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




//import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.google.common.base.Objects;

@Entity
public class Doctor {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
//	@GeneratedValue(generator = "generatorName")
//	@SequenceGenerator(name = "generatorName", sequenceName = "SEQ_NAME")
	private long doctorId;
	private String firstName;
	private String lastName;
	private String docUUID; //unique identifier for doctor
	private long userId;
	
	public Doctor(){		
	}
	
	public Doctor(String firstName, String lastName, String docUUID, long userId){
		super();
		this.firstName =firstName;
		this.lastName =lastName;
		this.docUUID = docUUID;
		this.userId = userId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getDoctorId() {
		return doctorId;
	}
	
	public void setDoctorId(long doctorId) {
		this.doctorId = doctorId;
	}
	
public String getDocUUID() {
		return docUUID;
	}

	public void setDocUUID(String docUUID) {
		this.docUUID = docUUID;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(firstName, lastName);
	}

	/**
	 * Two Doctors are considered equal if they have exactly the same values for
	 * their first and last names.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Doctor) {
			Doctor other = (Doctor) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(firstName, other.firstName) && Objects.equal(lastName, other.lastName);
		} else {
			return false;
		}
	}

}

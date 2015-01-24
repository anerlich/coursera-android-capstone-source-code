package au.com.azarel.symptomcheckerservice.repository;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
//import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.base.Objects;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long userId;
	private String firstName;
	private String lastName;
//    @Temporal(TemporalType.TIMESTAMP)
//    private Calendar dateOfBirth;
	private String userName;
	 @Enumerated(EnumType.STRING)
	private UserType userType;					//how is the patient's pain?

	
/*	@OneToMany(mappedBy="patient")
	Set<DoctorPatient>doctorPatients;
*/	
/*	@OneToMany(mappedBy="patient")
	Set<CheckIn> checkIns;
*/
/*	@OneToMany(mappedBy="patient")
	Set<PatientMedication> patientMedications;
*/
	public User() {		
	}
	
	public User(String firstName, String lastName,  String userName, UserType userType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.userType = userType;
	}


	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(firstName, lastName);
	}

	/**
	 * Two Patients are considered equal if they have exactly the same values for
	 * their name and doctorId.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User other = (User) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(firstName, other.firstName)
					&& Objects.equal(lastName, other.lastName);
		} else {
			return false;
		}
	}

}

package au.com.azarel.symptomcheckerservice.repository;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
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
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long patientId;
	private String firstName;
	private String lastName;
//  Use long to store date as it is the simplest option
	private long dateOfBirth;
	private String patUUID;
	private long userId;
	private boolean hasCriticalIssue;
	private boolean issueReported;
	
	public Patient() {		
	}
	
	public Patient(String firstName, String lastName, long dateOfBirth, 
			String patUUID, long userId, boolean hasCriticalIssue, boolean issueReported) {
		this.firstName =firstName;
		this.lastName =lastName;
		this.dateOfBirth = dateOfBirth;
		this.patUUID = patUUID;
		this.userId = userId;
		this.hasCriticalIssue = hasCriticalIssue;
		this.issueReported = issueReported;
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

	public long getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	
	public long getPatientId() {
		return patientId;
	}
	
	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	
	public String getPatUUID() {
		return patUUID;
	}

	public void setPatUUID(String patUUID) {
		this.patUUID = patUUID;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public boolean isHasCriticalIssue() {
		return hasCriticalIssue;
	}

	public void setHasCriticalIssue(boolean hasCriticalIssue) {
		this.hasCriticalIssue = hasCriticalIssue;
	}

	public boolean isIssueReported() {
		return issueReported;
	}

	public void setIssueReported(boolean issueReported) {
		this.issueReported = issueReported;
	}

	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(firstName, lastName);
	}

	/**
	 * Two Patients are considered equal if they have exactly the same values for
	 * their first and last names.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Patient) {
			Patient other = (Patient) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(firstName, other.firstName)
					&& Objects.equal(lastName, other.lastName);
		} else {
			return false;
		}
	}

}

package au.com.azarel.symptomcheckerservice.repository;
/*
 * Represents a response to the "did you take your [Lortab/Oxycodone/...] medication
 * question at each Check In made by a patient
 */
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.common.base.Objects;

@Entity
public class CheckInMedication {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long checkInMedicationId;
	
	@ManyToOne //(fetch=FetchType.LAZY)
	@JoinColumn(name="checkInId")
	CheckIn checkIn;
	@ManyToOne //(fetch=FetchType.LAZY)
	@JoinColumn(name="patientMedicationId")
	PatientMedication patientMedication;
	boolean tookIt;
	String description;
	long tookItTime;
	long cimCheckInId;
	long cimPatientMedicationId;
	
	public CheckInMedication(){
	}
	
	public CheckInMedication(CheckIn checkIn, PatientMedication patientMedication,
			String description, boolean tookIt, long tookItTime,long cimCheckInId,
			long cimPatientMedicationId){
		this.checkIn = checkIn;
		this.patientMedication = patientMedication;
		this.description = description;
		this.tookIt = tookIt;
		this.tookItTime = tookItTime;
		this.cimCheckInId = cimCheckInId;
		this.cimPatientMedicationId = cimPatientMedicationId;
	}

	
	public long getCheckInMedicationId() {
		return checkInMedicationId;
	}

	public void setCheckInMedicationId(long checkInMedicationId) {
		this.checkInMedicationId = checkInMedicationId;
	}
	
	public boolean getTookIt() {
		return tookIt;
	}
	
	public void setTookIt(boolean tookIt) {
		this.tookIt = tookIt;
	}

	public long getTookItTime() {
		return tookItTime;
	}
	
	public void setTookItTime(long tookItTime) {
		this.tookItTime = tookItTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CheckIn getCheckIn() {
		return checkIn;
	}
	
	public void setCheckIn(CheckIn checkIn) {
		this.checkIn = checkIn;
/*		if (!checkIn.checkInMedications.contains(this)){
			checkIn.checkInMedications.add(this);
		}
*/	}
	
	public PatientMedication getPatientMedication() {
		return patientMedication;
	}
	
	public void setPatientMedication(PatientMedication patientMedication) {
		this.patientMedication = patientMedication;
	}
	


	public long getCimCheckInId() {
		return cimCheckInId;
	}

	public void setCimCheckInId(long cimCheckInId) {
		this.cimCheckInId = cimCheckInId;
	}

	public long getCimPatientMedicationId() {
		return cimPatientMedicationId;
	}

	public void setCimPatientMedicationId(long cimPatientMedicationId) {
		this.cimPatientMedicationId = cimPatientMedicationId;
	}

	/**
	 * Two Videos will generate the same hashcode if they have exactly the same
	 * values for their patient's id and the check in time.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(checkIn.getCheckInId(), patientMedication.getPatientMedicationId());
	}

	/**
	 * Two CheckIns are considered equal if they have exactly the same values for
	 * their patient's Id and checkInTime.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CheckInMedication) {
			CheckInMedication other = (CheckInMedication) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(checkIn.getCheckInId(), other.checkIn.getCheckInId())
					&& Objects.equal(patientMedication.getPatientMedicationId(), 
							other.patientMedication.getPatientMedicationId());
		} else {
			return false;
		}
	}

}

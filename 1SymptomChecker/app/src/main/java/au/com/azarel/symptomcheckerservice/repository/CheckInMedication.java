package au.com.azarel.symptomcheckerservice.repository;
/*
 * Represents a response to the "did you take your [Lortab/Oxycodone/...] medication
 * question at each Check In made by a patient
 */
import java.util.Date;





import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class CheckInMedication implements Parcelable {
	private long checkInMedicationId;
	
	CheckIn checkIn;
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

	public CheckInMedication(Parcel in) {
		checkInMedicationId=in.readLong();
		description = in.readString();
		tookIt = (in.readInt() == 0) ? false: true;
		tookItTime = in.readLong();
		cimCheckInId = in.readLong();
		cimPatientMedicationId = in.readLong();
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
	}
	
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

	public static final Parcelable.Creator<CheckInMedication> CREATOR = 
			new Parcelable.Creator<CheckInMedication>() { 
		public CheckInMedication createFromParcel(Parcel in) { 
			return new CheckInMedication(in); 
			} 
	
		public CheckInMedication[] newArray(int size) { 
			return new CheckInMedication[size]; 
			} 
	}; 
	
	@Override 
	public int describeContents() { 
		return 0; 
		}
	
	  @Override
	  public void writeToParcel(Parcel dest, int flags) {
		  dest.writeLong(checkInMedicationId);
		  dest.writeInt((tookIt)? 1 : 0);
	      dest.writeString(description);
	      dest.writeLong(tookItTime);
	      dest.writeLong(cimCheckInId);
		  dest.writeLong(cimPatientMedicationId);        
	  }

}

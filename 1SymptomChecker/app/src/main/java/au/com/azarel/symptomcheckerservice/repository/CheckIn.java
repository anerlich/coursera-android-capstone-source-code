package au.com.azarel.symptomcheckerservice.repository;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;





import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

/**
 * A simple object to represent a check in.
 * 
 * @author student from original code by jules
 * 
 */

public class CheckIn implements Parcelable {

	private long checkInId;
	
	private Patient patient; 			//object referring to the patient who is checking in
	
	private long ciPatientId; 			//copy of patient id for lookup purposes
	private String description;
	private long checkInTime; 			//time of check in
	private Pain pain;					//how is the patient's pain?
	private EatingAffect eatingAffect;	//what effect is their condition having on their eating?
	private boolean tookMedication;		//did they take their medication?

	// constructors
	
	public CheckIn() {
	}

	public CheckIn(Patient patient,long ciPatientId, String description,long checkInTime, Pain pain, EatingAffect eatingAffect,boolean tookMedication) {
		super();
		this.patient = patient;
		this.ciPatientId=ciPatientId;
		this.description = description;
		this.checkInTime = checkInTime;
		this.pain = pain;
		this.eatingAffect = eatingAffect;
		this.tookMedication = tookMedication;
	}

	public CheckIn(Parcel in) {
		checkInId=in.readLong();
		ciPatientId = in.readLong();
		description = in.readString();
		checkInTime = in.readLong();
        try {
            pain = Pain.valueOf(in.readString());
        } catch (IllegalArgumentException x) {
            pain = null;
        }
        try {
            eatingAffect = EatingAffect.valueOf(in.readString());
        } catch (IllegalArgumentException x) {
        	eatingAffect = null;
        }
		tookMedication = (in.readInt()==1)?true:false;
	}

	public Patient getPatient() {
		return patient;
	}
	
	//getters/setters
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public long getCiPatientId() {
		return ciPatientId;
	}

	public void setCiPatientId(long ciPatientId) {
		this.ciPatientId = ciPatientId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(long checkInTime) {
		this.checkInTime = checkInTime;
	}

	public Pain getPain() {
		return pain;
	}

	public void setPain(Pain pain) {
		this.pain = pain;
	}

		public EatingAffect getEatingAffect() {
		return eatingAffect;
	}

	public void setEatingAffect(EatingAffect eatingAffect) {
		this.eatingAffect = eatingAffect;
	}

	
	public boolean getTookMedication() {
		return tookMedication;
	}

	public void setTookMedication(boolean tookMedication) {
		this.tookMedication = tookMedication;
	}
	

	public long getCheckInId() {
		return checkInId;
	}

	public void setCheckInId(long checkInId) {
		this.checkInId = checkInId;
	}
	
/*	public Set<CheckInMedication> getCheckInMedications() {
		return checkInMedications;
	}
	
	public void setCheckInMedications(Set<CheckInMedication> checkInMedications){
		this.checkInMedications = checkInMedications;
	}
	
	public void addCheckInMedication(CheckInMedication checkInMedication) {
		this.checkInMedications.add(checkInMedication);
		if (!checkInMedication.getCheckIn().equals(this)) {
			checkInMedication.setCheckIn(this);
		}
	}

*/	/**
	 * Two Check Ins will generate the same hashcode if they have exactly the same
	 * values for their patient's id and the check in time.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(patient.getPatientId(), checkInTime);
	}

	/**
	 * Two CheckIns are considered equal if they have exactly the same values for
	 * their patient's Id and checkInTime.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CheckIn) {
			CheckIn other = (CheckIn) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(patient.getPatientId(), other.patient.getPatientId())
					&& Objects.equal(checkInTime, other.checkInTime);
		} else {
			return false;
		}
	}

	public static final Parcelable.Creator<CheckIn> CREATOR = new Parcelable.Creator<CheckIn>() { 
		public CheckIn createFromParcel(Parcel in) { 
			return new CheckIn(in); 
			} 
	
		public CheckIn[] newArray(int size) { 
			return new CheckIn[size]; 
			} 
	}; 
	
	@Override 
	public int describeContents() { 
		return 0; 
		}
	
	  @Override
	  public void writeToParcel(Parcel dest, int flags) {
		  dest.writeLong(checkInId);
	    dest.writeLong(ciPatientId);
	    dest.writeString(description);
	    dest.writeLong(checkInTime);
	    dest.writeString(pain.name());
		dest.writeString(eatingAffect.name()); 
		dest.writeInt((tookMedication)?1:0);
	  }

}

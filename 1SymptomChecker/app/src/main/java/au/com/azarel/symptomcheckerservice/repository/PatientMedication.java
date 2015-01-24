package au.com.azarel.symptomcheckerservice.repository;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class PatientMedication implements Parcelable {
	
	private long patientMedicationId;
	
	private Patient patient;
	private long pmPatientId;
	
	private String medication;
	
	public PatientMedication(){
	}
	
	public PatientMedication(Patient patient, String medication, long pmPatientId){
		this.patient = patient;
		this.medication = medication;
		this.pmPatientId=pmPatientId;
	}
	public PatientMedication(Parcel in) {
		patientMedicationId = in.readLong();
		// don't bother with Patient as calling code will already have it
		pmPatientId = in.readLong();
		medication = in.readString();
	}
	
	
	public long getPatientMedicationId() {
		return patientMedicationId;
	}
	
	public void setPatientMedicationId(long patientMedicationId) {
		this.patientMedicationId = patientMedicationId;
	}

	public String getMedication() {
		return medication;
	}
	
	public void setMedication(String medication) {
		this.medication = medication;
	}

	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
/*		if (!patient.getPatientMedications().contains(this)) {
			patient.getPatientMedications().add(this);
		}
*/	}

	public long getPmPatientId() {
		return pmPatientId;
	}

	public void setPmPatientId(long pmPatientId) {
		this.pmPatientId = pmPatientId;
	}

	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(patient.getPatientId(), medication);
	}

	/**
	 * Two CheckIns are considered equal if they have exactly the same values for
	 * their patient's Id and medication.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PatientMedication) {
			PatientMedication other = (PatientMedication) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(patient.getPatientId(), other.patient.getPatientId())
					&& Objects.equal(medication, other.medication);
		} else {
			return false;
		}
	}
	
	// Parcelable setup code
	public static final Parcelable.Creator<PatientMedication> CREATOR = 
												new Parcelable.Creator<PatientMedication>() { 
		public PatientMedication createFromParcel(Parcel in) { 
			return new PatientMedication(in); 
			} 
	
		public PatientMedication[] newArray(int size) { 
			return new PatientMedication[size]; 
			} 
	}; 
	
	@Override 
	public int describeContents() { 
		return 0; 
		}
	
	  @Override
	  public void writeToParcel(Parcel dest, int flags) {
		  dest.writeLong(patientMedicationId);
	    dest.writeLong(pmPatientId);
	    dest.writeString(medication);
	  }

}

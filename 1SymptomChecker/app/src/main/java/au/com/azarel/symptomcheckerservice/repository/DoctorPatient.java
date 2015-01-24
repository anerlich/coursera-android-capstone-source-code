package au.com.azarel.symptomcheckerservice.repository;

import java.util.Set;



import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class DoctorPatient  implements Parcelable {
	long doctorPatientId;
	private Doctor doctor;
	private Patient patient;
	private long dpDoctorId;
	private long dpPatientId;
	private String description;
	
	public DoctorPatient(){
		
	}

	public DoctorPatient(Doctor doctor, Patient patient, String description,long doctorId, long patientId){
		this.doctor = doctor;
		this.patient=patient;
		this.description = description;
		this.dpDoctorId=doctorId;
		this.dpPatientId=patientId;
	}

	public DoctorPatient(Parcel in) {
		doctorPatientId=in.readLong();
		description = in.readString();
		dpDoctorId = in.readLong();
		dpPatientId = in.readLong();
	}

	public long getDoctorPatientId() {
		return doctorPatientId;
	}
	
	public void setDoctorPatientId(long doctorPatientId) {
		this.doctorPatientId = doctorPatientId;
	}

	public long getDpDoctorId() {
		return dpDoctorId;
	}
	
	public void setDpDoctorId(long dpDoctorId) {
		this.dpDoctorId = dpDoctorId;
	}
	
	public long getDpPatientId() {
		return dpPatientId;
	}
	
	public void setDpPatientId(long dpPatientId) {
		this.dpPatientId = dpPatientId;
	}

	public Doctor getDoctor() {
		return doctor;
	}
	
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
/*		if (!doctor.getDoctorPatients().contains(this)) {
			doctor.getDoctorPatients().add(this);
		}
*/	}

	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
/*		if (!patient.getDoctorPatients().contains(this)) {
			patient.getDoctorPatients().add(this);
		}
*/	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(doctor.getDoctorId(), patient.getPatientId());
	}

	/**
	 * Two Patients are considered equal if they have exactly the same values for
	 * their first and last names.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DoctorPatient) {
			DoctorPatient other = (DoctorPatient) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(doctor.getDoctorId(), other.doctor.getDoctorId()) 
					&& Objects.equal(patient.getPatientId(), other.patient.getPatientId());
		} else {
			return false;
		}
	}

	public static final Parcelable.Creator<DoctorPatient> CREATOR = 
			new Parcelable.Creator<DoctorPatient>() { 
		public DoctorPatient createFromParcel(Parcel in) { 
			return new DoctorPatient(in); 
			} 
	
		public DoctorPatient[] newArray(int size) { 
			return new DoctorPatient[size]; 
			} 
	}; 
	
	@Override 
	public int describeContents() { 
		return 0; 
		}
	
	  @Override
	  public void writeToParcel(Parcel dest, int flags) {
		  dest.writeLong(doctorPatientId);
	    dest.writeString(description);
	    dest.writeLong(dpDoctorId);
		  dest.writeLong(dpPatientId);        
	  }

}

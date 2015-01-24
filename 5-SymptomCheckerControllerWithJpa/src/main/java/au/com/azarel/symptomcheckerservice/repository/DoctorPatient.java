package au.com.azarel.symptomcheckerservice.repository;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.common.base.Objects;

@Entity
public class DoctorPatient {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long doctorPatientId;
	@ManyToOne //(fetch=FetchType.LAZY)
	@JoinColumn(name="doctorId")
	private Doctor doctor;
	@ManyToOne //(fetch=FetchType.LAZY)
	@JoinColumn(name="patientId")
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

/*	public DoctorPatient(long doctorId, long patientId, String description){
		this.doctorId = doctorId;
		this.patientId=patientId;
		this.description = description;
	}
*/
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

}

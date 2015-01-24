package au.com.azarel.symptomcheckerservice.repository;

import javax.persistence.Entity;
//import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.common.base.Objects;

@Entity
public class PatientMedication {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long patientMedicationId;
	
	@ManyToOne //(fetch=FetchType.LAZY)
	@JoinColumn(name="patientId")
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

}

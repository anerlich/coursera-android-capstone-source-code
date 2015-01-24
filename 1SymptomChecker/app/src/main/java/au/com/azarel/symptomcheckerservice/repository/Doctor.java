package au.com.azarel.symptomcheckerservice.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class Doctor implements Parcelable {
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

//		this.doctorPatients = new ArrayList<DoctorPatient>();
	}

	public Doctor(Parcel in) {
		doctorId=in.readLong();
		firstName = in.readString();
		lastName = in.readString();
		docUUID = in.readString();
		userId = in.readLong();
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
	
public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getDocUUID() {
		return docUUID;
	}

	public void setDocUUID(String docUUID) {
		this.docUUID = docUUID;
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
		if (obj instanceof Doctor) {
			Doctor other = (Doctor) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(firstName, other.firstName) && Objects.equal(lastName, other.lastName);
		} else {
			return false;
		}
	}

	public static final Parcelable.Creator<Doctor> CREATOR = new Parcelable.Creator<Doctor>() { 
		public Doctor createFromParcel(Parcel in) { 
			return new Doctor(in); 
			} 
	
		public Doctor[] newArray(int size) { 
			return new Doctor[size]; 
			} 
	}; 
	
	@Override 
	public int describeContents() { 
		return 0; 
		}
	
	  @Override
	  public void writeToParcel(Parcel dest, int flags) {
		  dest.writeLong(doctorId);
	    dest.writeString(firstName);
	    dest.writeString(lastName);
	    dest.writeString(docUUID);
		  dest.writeLong(userId);        
	  }

}

package au.com.azarel.symptomcheckerservice.repository;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class User implements Parcelable {

	private long userId;
	private String firstName;
	private String lastName;
	private String userName;
	private UserType userType;					
	

	public User() {		
	}
	
	public User(String firstName, String lastName,  String userName, UserType userType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.userType = userType;
	}

	  private User(Parcel in) {
		  	userId=in.readLong();
		    firstName = in.readString();
		    lastName = in.readString();
		    userName = in.readString();
	        try {
	            userType = UserType.valueOf(in.readString());
	        } catch (IllegalArgumentException x) {
	            userType = null;
	        }
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

	// Parcelable setup code
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() { 
		public User createFromParcel(Parcel in) { 
			return new User(in); 
			} 
	
		public User[] newArray(int size) { 
			return new User[size]; 
			} 
	}; 
	
	@Override 
	public int describeContents() { 
		return 0; 
		}
	
	  @Override
	  public void writeToParcel(Parcel dest, int flags) {
		  dest.writeLong(userId);
	    dest.writeString(firstName);
	    dest.writeString(lastName);
	    dest.writeString(userName);
        dest.writeString(userType.name());       
	  }



}

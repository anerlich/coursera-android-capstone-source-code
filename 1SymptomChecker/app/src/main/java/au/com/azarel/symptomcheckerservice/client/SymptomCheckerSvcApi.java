package au.com.azarel.symptomcheckerservice.client;

import java.util.Collection;
import java.util.List;

import au.com.azarel.symptomcheckerservice.repository.*;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * This interface defines an API for a SymptomCheckerSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * 
 * Adapted from Mobile Cloud example code:
 * @author jules
 *
 */
public interface SymptomCheckerSvcApi {
	
	public static final String TITLE_PARAMETER = "title";
	public static final String ID_PARAMETER = "id";
	
	public static final String LOGIN_PATH = "/login";
	
	public static final String LOGOUT_PATH = "/logout";
	
	public static final String PASSWORD_PARAMETER = "password";

	public static final String USERNAME_PARAMETER = "username";

	@FormUrlEncoded
	@POST(LOGIN_PATH)
	public Void login(@Field(USERNAME_PARAMETER) String username, @Field(PASSWORD_PARAMETER) String pass);
	
	@GET(LOGOUT_PATH)
	public Void logout();

	
	@GET("/doctor")
	public Collection<Doctor> getDoctorList();

	@POST("/doctor")
	public Doctor addDoctor(@Body Doctor d);
	
	@GET("/doctor/{id}")
	public Doctor getDoctorById(@Path("id") long id);


	@GET("/patient")
	public Collection<Patient> getPatientList();

	@POST("/patient")
	public Patient addPatient(@Body Patient p);
	
	@GET("/patient/{id}")
	public Patient getPatientById(@Path("id") long id);

	@PATCH("/patient/{id}/updatePatientReportStatus")
	public Patient UpdatePatientReportStatus(@Path("id") long id);

	@GET("/doctorpatient")
	public Collection<DoctorPatient> getDoctorPatientList();

	@POST("/doctorpatient")
	public DoctorPatient addDoctorPatient(@Body DoctorPatient dp);

	@GET("/doctorpatient/findDoctorPatientByDpDoctorId")
	public Collection<DoctorPatient> findDoctorPatientByDpDoctorId(@Query("dpDoctorId") long dpDoctorId);

	/*	@GET("/doctorpatient/findByPatientId")
	public Collection<DoctorPatient> findByPatientId(@Query("patientId") long patientId);
*/
	@GET("/checkin")
	public Collection<CheckIn> getCheckInList();

	@POST("/checkin")
	public CheckIn addCheckIn(@Body CheckIn ci);

	@GET("/checkin/findCheckInByCiPatientId")
	public Collection<CheckIn> findCheckInByCiPatientId(@Query("ciPatientId") long ciPatientId,
			@Query("checkInTime") long checkInTime);

	@GET("/checkin/findCheckInByCiPatientIdPainType")
	public Collection<CheckIn> findCheckInByCiPatientIdPainType(
			@Query("ciPatientId") long ciPatientId,
			@Query("checkInTime") long checkInTime,
			@Query("pain") List<Pain>pain);

	@GET("/patientmedication")
	public Collection<PatientMedication> getPatientMedicationList();

	@POST("/patientmedication")
	public PatientMedication addPatientMedication(@Body PatientMedication pm);

	@GET("/patientmedication/findPatientMedicationByPmPatientId")
	public Collection<PatientMedication> findPatientMedicationByPmPatientId(@Query("pmPatientId") long pmPatientId);

	@GET("/checkinmedication")
	public Collection<CheckInMedication> getCheckInMedicationList();

	@POST("/checkinmedication")
	public CheckInMedication addCheckInMedication(@Body CheckInMedication cim);

	@GET("/checkinmedication/findCheckInMedicationByCimCheckInId")
	public Collection<CheckInMedication> findCheckInMedicationByCimCheckInId(@Query("cimCheckInId") long cimCheckInId);

	@GET("/user/findUserByUserName")
	public Collection<User> findUserByUserName(@Query("userName") String userName);
	
	@GET("/doctor/findDoctorByUserId")
	public Collection<Doctor> findDoctorByUserId(@Query("userId") long userId);
	
	@GET("/patient/findPatientByUserId")
	public Collection<Patient> findPatientByUserId(@Query("userId") long userId);

	@GET("/patient/findPatientByLastNameAndDoctorId")
	public Collection<Patient> findPatientByLastNameAndDoctorId(
			@Query("lastName") String lastName,
			@Query("doctorId") long doctorId);

}

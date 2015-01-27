package au.com.azarel.symptomcheckerservice.client;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.repository.query.Param;

import au.com.azarel.symptomcheckerservice.repository.*;
import retrofit.client.Response;
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
 * URL into function calls
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

	// log in/out
	
	@FormUrlEncoded
	@POST(LOGIN_PATH)
	public Response login(@Field(USERNAME_PARAMETER) String username, @Field(PASSWORD_PARAMETER) String pass);// throws UnauthorizedException;
	
	@GET(LOGOUT_PATH)
	public Response logout();

	// Doctor
	
	@GET("/doctor")
	public Collection<Doctor> getDoctorList();

	@POST("/doctor")
	public Doctor addDoctor(@Body Doctor d);
	
	@GET("/doctor/{id}")
	public Doctor getDoctorById(@Path("id") long id);

	@GET("/doctor/findDoctorByUserId")
	public Collection<Doctor> findDoctorByUserId(@Query("userId") long userId);	

	//Patient

	@GET("/patient")
	public Collection<Patient> getPatientList();

	@POST("/patient")
	public Patient addPatient(@Body Patient p);

	@GET("/patient/{id}")
	public Patient getPatientById(@Path("id") long id);

	@GET("/patient/findPatientByLastNameAndDoctorId")
	public Collection<Patient> findPatientByLastNameAndDoctorId(
			@Query("lastName") String lastName,
			@Query("doctorId") long doctorId);
	
	@PATCH("/patient/{id}/updatePatientReportStatus")
	public Patient updatePatientReportStatus(@Path("id") long id);

	@GET("/patient/findPatientByUserId")
	public Collection<Patient> findPatientByUserId(@Query("userId") long userId);
	
	//DoctorPatient

	@GET("/doctorpatient")
	public Collection<DoctorPatient> getDoctorPatientList();

	@POST("/doctorpatient")
	public DoctorPatient addDoctorPatient(@Body DoctorPatient dp);

	@GET("/doctorpatient/findDoctorPatientByDpDoctorId")
	public Collection<DoctorPatient> findDoctorPatientByDpDoctorId(@Query("dpDoctorId") long dpDoctorId);

	// CheckIn
	
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
			@Query("pain") List<Pain> pain);

	@GET("/checkin/findCheckInByCiPatientIdEatingAffectType")
	public Collection<CheckIn> findCheckInByCiPatientIdEatingAffectType(
			@Query("ciPatientId") long ciPatientId,
			@Query("checkInTime") long checkInTime,
			@Query("eatingAffect") List<EatingAffect> eatingAffect);
	
	//PatientMedication

	@GET("/patientmedication")
	public Collection<PatientMedication> getPatientMedicationList();

	@POST("/patientmedication")
	public PatientMedication addPatientMedication(@Body PatientMedication pm);
	
	@GET("/patientmedication/findPatientMedicationByPmPatientId")
	public Collection<PatientMedication> findPatientMedicationByPmPatientId(@Query("pmPatientId") long pmPatientId);

	//CheckInMedication
	
	@GET("/checkinmedication")
	public Collection<CheckInMedication> getCheckInMedicationList();

	@POST("/checkinmedication")
	public CheckInMedication addCheckInMedication(@Body CheckInMedication cim);

	@GET("/checkinmedication/findCheckInMedicationByCimCheckInId")
	public Collection<CheckInMedication> findCheckInMedicationByCimCheckInId(@Query("cimCheckInId") long cimCheckInId);

	//User

	@GET("/user")
	public Collection<User> getUserList();

	@POST("/user")
	public User addUser(@Body User u);
	
	@GET("/user/{id}")
	public User getUserById(@Path("id") long id);

	@GET("/user/findUserByUserName")
	public Collection<User> findUserByUserName(@Query("userName") String userName);
	
}

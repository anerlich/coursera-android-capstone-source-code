package org.magnum.mobilecloud.integration.test;

import static org.junit.Assert.assertTrue;

import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.Collection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;
//import org.magnum.mobilecloud.video.TestData;
















import au.com.azarel.symptomcheckerservice.client.SymptomCheckerSvcApi;
import au.com.azarel.symptomcheckerservice.repository.*;
import retrofit.ErrorHandler;
//import au.com.azarel.symptomcheckerservice.SecurityConfiguration;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Response;

import org.magnum.mobilecloud.integration.test.UnauthorizedException;
/**
 * 
 * This integration test sends a POST request to the VideoServlet to add a new video 
 * and then sends a second GET request to check that the video showed up in the list
 * of videos. Actual network communication using HTTP is performed with this test.

 * The test requires that the VideoSvc be running first (see the directions in
 * the README.md file for how to launch the Application).
 * 
 * To run this test, right-click on it in Eclipse and select
 * "Run As"->"JUnit Test"
 * 
 * Pay attention to how this test that actually uses HTTP and the test that just
 * directly makes method calls on a VideoSvc object are essentially identical.
 * All that changes is the setup of the videoService variable. Yes, this could
 * be refactored to eliminate code duplication...but the goal was to show how
 * much Retrofit simplifies interaction with our service!
 * 
 * @author jules
 *
 */
public class SymptomCheckerSvcClientApiTest {

	private final String TEST_URL = "https://localhost:8443";

/*	SymptomCheckerAuthenticator sca =new SymptomCheckerAuthenticator();
	PasswordAuthentication pa = sca.getPasswordAuthentication();
	byte[] encodedBytes = Base64.encodeBase64("coursera:changeit".getBytes());
	final String basicAuth = "Basic " + encodedBytes.toString();
    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            //request.addHeader("Authorization", "Basic NTMzYWM5MmYxZTIxYmMwMDAwZWJlNTBlOmQ=");
            request.addHeader("Authorization", basicAuth);
            //getToken("coursera", "changeit");
        }
    };
*/
	class MyErrorHandler implements ErrorHandler {
		  @Override public Throwable handleError(RetrofitError cause) {
			  if (cause.isNetworkError()) {
				  return cause;
			  }
		    Response r = cause.getResponse();
		    if (r != null && r.getStatus() != 200) {
		      System.out.println("Login Unsuccessful - response was: " + r.getStatus() + " " + r.getReason());
		    }
		    return cause;
		  }
		}

	private SymptomCheckerSvcApi symptomCheckerService = new RestAdapter.Builder()
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setErrorHandler(new MyErrorHandler())
//			.setRequestInterceptor(requestInterceptor)
			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
			.create(SymptomCheckerSvcApi.class);

	
	/**
	 * This test creates a Video, adds the Video to the VideoSvc, and then
	 * checks that the Video is included in the list when getVideoList() is
	 * called.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVideoAddAndList() throws Exception {
		Response r;
		try{
		r = symptomCheckerService.login("doctor1", "changeit");
		} catch (Exception e) {
			
			return;
		}
		//Assert.assertEquals(200, r.getStatus());
		//Doctor doc1 = new Doctor("Joe","Blake");
		User userDoc1 = symptomCheckerService.addUser(new User("Zakk","Wylde","doctor1",UserType.DOCTOR));
		User userDoc2 = symptomCheckerService.addUser(new User("Billy","Zoom","doctor2",UserType.DOCTOR));
		User userPat1 = symptomCheckerService.addUser(new User("Glenn","Tipton","patient1",UserType.PATIENT));
		User userpat6 = symptomCheckerService.addUser(new User("Sarah","Tipton","patient2",UserType.PATIENT));
		User userPat3 = symptomCheckerService.addUser(new User("Adam","Jones","patient3",UserType.PATIENT));
		User userPat4 = symptomCheckerService.addUser(new User("Justin","Chancellor","patient4",UserType.PATIENT));
		User userPat5 = symptomCheckerService.addUser(new User("Mary","Falconer","patient5",UserType.PATIENT));
		User userPat6 = symptomCheckerService.addUser(new User("Kate","Bush","patient6",UserType.PATIENT));
		User userPat7 = symptomCheckerService.addUser(new User("James","Keenan","patient7",UserType.PATIENT));
		User userPat8 = symptomCheckerService.addUser(new User("Danny","Carey","patient8",UserType.PATIENT));
		UUID doc1UUID = UUID.randomUUID();
		UUID doc2UUID = UUID.randomUUID();
		Doctor doc1 = symptomCheckerService.addDoctor(new Doctor("Zakk","Wylde",doc1UUID.toString(),userDoc1.getUserId()));
		Doctor doc2 = symptomCheckerService.addDoctor(new Doctor("Billy","Zoom",doc2UUID.toString(),userDoc2.getUserId()));
/*		Collection<User> user1s = symptomCheckerService.findUserByUserName("doctor1");
		User[] user1as = user1s.toArray(new User[0]);
*/	
		//User user1 = symptomCheckerService.getUserById(id)
		Collection<Doctor> doc1s = symptomCheckerService.findDoctorByUserId(userDoc1.getUserId());
		Date datNow = new Date();
		//Patient pat1 = new Patient("John", "Brown", datNow.getTime());
		UUID patUUID = UUID.randomUUID();
		Patient pat1 =symptomCheckerService.addPatient(new Patient("Glenn","Tipton", datNow.getTime(),patUUID.toString(),userPat1.getUserId(),false,false));
		patUUID = UUID.randomUUID();
		Patient pat2 =symptomCheckerService.addPatient(new Patient("Sarah","Tipton", datNow.getTime(),patUUID.toString(),userpat6.getUserId(),false,false));
		patUUID = UUID.randomUUID();
		Patient pat3 =symptomCheckerService.addPatient(new Patient("Adam","Jones", datNow.getTime(),patUUID.toString(),userPat3.getUserId(),false,false));
		patUUID = UUID.randomUUID();
		Patient pat4 =symptomCheckerService.addPatient(new Patient("Justin","Chancellor", datNow.getTime(),patUUID.toString(),userPat4.getUserId(),false,false));
		patUUID = UUID.randomUUID();
		Patient pat5 =symptomCheckerService.addPatient(new Patient("Mary","Falconer", datNow.getTime(),patUUID.toString(),userPat5.getUserId(),false,false));
		patUUID = UUID.randomUUID();
		Patient pat6 =symptomCheckerService.addPatient(new Patient("Kate","Bush", datNow.getTime(),patUUID.toString(),userPat6.getUserId(),false,false));
		patUUID = UUID.randomUUID();
		Patient pat7 =symptomCheckerService.addPatient(new Patient("James","Keenan", datNow.getTime(),patUUID.toString(),userPat7.getUserId(),false,false));
		patUUID = UUID.randomUUID();
		Patient pat8 =symptomCheckerService.addPatient(new Patient("Danny","Carey", datNow.getTime(),patUUID.toString(),userPat8.getUserId(),false,false));
		
		
		DoctorPatient dp1 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc1,pat1,"xx",doc1.getDoctorId(),pat1.getPatientId()));
		DoctorPatient dp2 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc1,pat6,"xx",doc1.getDoctorId(),pat6.getPatientId()));
		DoctorPatient dp3 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc1,pat3,"xx",doc1.getDoctorId(),pat3.getPatientId()));
		DoctorPatient dp4 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc1,pat4,"xx",doc1.getDoctorId(),pat4.getPatientId()));
		DoctorPatient dp5 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc2,pat5,"xx",doc2.getDoctorId(),pat5.getPatientId()));
		DoctorPatient dp6 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc2,pat6,"xx",doc2.getDoctorId(),pat6.getPatientId()));
		DoctorPatient dp7 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc2,pat7,"xx",doc2.getDoctorId(),pat7.getPatientId()));
		//Glenn Tipton is a patient of both doctors
		DoctorPatient dp8 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc2,pat1,"xx",doc2.getDoctorId(),pat1.getPatientId()));
		DoctorPatient dp9 = symptomCheckerService.addDoctorPatient(new DoctorPatient(doc2,pat8,"xx",doc2.getDoctorId(),pat8.getPatientId()));
		Collection<Patient> pat1s = symptomCheckerService.getPatientList();
		Collection<DoctorPatient> docpat6s = symptomCheckerService.findDoctorPatientByDpDoctorId(doc1.getDoctorId());
		DoctorPatient[] docpat3s = docpat6s.toArray(new DoctorPatient[0]);
		Patient dpp4 = docpat3s[0].getPatient();
		Patient dpp5 = symptomCheckerService.getPatientById(dpp4.getPatientId());
/*		dpp5.setHasCriticalIssue(true);
		dpp5.setIssueReported(true);
		dpp5 = symptomCheckerService.addPatient(dpp5); //will add new patient or update existing?
*/		
		Calendar cal1 = Calendar.getInstance();
		System.out.println(cal1.getTimeInMillis());
		Calendar calMinus3 = Calendar.getInstance();
		calMinus3.add(Calendar.HOUR, -3);
		System.out.println(calMinus3.getTimeInMillis());
		Calendar calMinus6 = Calendar.getInstance();
		calMinus6.add(Calendar.HOUR, -6);
		Calendar calMinus9 = Calendar.getInstance();
		calMinus9.add(Calendar.HOUR, -9);
		Calendar calMinus12 = Calendar.getInstance();
		calMinus12.add(Calendar.HOUR, -12);
		Calendar calMinus15 = Calendar.getInstance();
		calMinus15.add(Calendar.HOUR, -15);
		Calendar calMinus20 = Calendar.getInstance();
		calMinus20.add(Calendar.HOUR, -20);
		
		// patient 1 has no issues
		CheckIn c1 = symptomCheckerService.addCheckIn(new CheckIn(pat1, pat1.getPatientId(), "chk", cal1.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));
		CheckIn c2 = symptomCheckerService.addCheckIn(new CheckIn(pat1, pat1.getPatientId(), "chk", calMinus3.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));
		CheckIn c3 = symptomCheckerService.addCheckIn(new CheckIn(pat1, pat1.getPatientId(), "chk", calMinus6.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));
		CheckIn c4 = symptomCheckerService.addCheckIn(new CheckIn(pat1, pat1.getPatientId(), "chk", calMinus9.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));
		CheckIn c5 = symptomCheckerService.addCheckIn(new CheckIn(pat1, pat1.getPatientId(), "chk", calMinus12.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));
		CheckIn c6 = symptomCheckerService.addCheckIn(new CheckIn(pat1, pat1.getPatientId(), "chk", calMinus15.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));
		CheckIn c7 = symptomCheckerService.addCheckIn(new CheckIn(pat1, pat1.getPatientId(), "chk", calMinus20.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));

		//patient2 has eating issues for 20 hours
		CheckIn c11 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", cal1.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c12 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus3.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c13 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus6.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c14 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus9.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c15 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus12.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c16 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus15.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c17 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus20.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));

		//patient3 has severe pain issues for 20 hours
		CheckIn c21 = symptomCheckerService.addCheckIn(new CheckIn(pat3, pat3.getPatientId(), "chk", cal1.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c22 = symptomCheckerService.addCheckIn(new CheckIn(pat3, pat3.getPatientId(), "chk", calMinus3.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c23 = symptomCheckerService.addCheckIn(new CheckIn(pat3, pat3.getPatientId(), "chk", calMinus6.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c24 = symptomCheckerService.addCheckIn(new CheckIn(pat3, pat3.getPatientId(), "chk", calMinus9.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c25 = symptomCheckerService.addCheckIn(new CheckIn(pat3, pat3.getPatientId(), "chk", calMinus12.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c26 = symptomCheckerService.addCheckIn(new CheckIn(pat3, pat3.getPatientId(), "chk", calMinus15.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c27 = symptomCheckerService.addCheckIn(new CheckIn(pat3, pat3.getPatientId(), "chk", calMinus20.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));

		//patient4 has moderate pain issues for 20 hours
		CheckIn c31 = symptomCheckerService.addCheckIn(new CheckIn(pat4, pat4.getPatientId(), "chk", cal1.getTimeInMillis(), Pain.MODERATE, EatingAffect.NONE, false));
		CheckIn c32 = symptomCheckerService.addCheckIn(new CheckIn(pat4, pat4.getPatientId(), "chk", calMinus3.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, true));
		CheckIn c33 = symptomCheckerService.addCheckIn(new CheckIn(pat4, pat4.getPatientId(), "chk", calMinus6.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, false));
		CheckIn c34 = symptomCheckerService.addCheckIn(new CheckIn(pat4, pat4.getPatientId(), "chk", calMinus9.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, true));
		CheckIn c35 = symptomCheckerService.addCheckIn(new CheckIn(pat4, pat4.getPatientId(), "chk", calMinus12.getTimeInMillis(), Pain.MODERATE, EatingAffect.CANT_EAT, false));
		CheckIn c36 = symptomCheckerService.addCheckIn(new CheckIn(pat4, pat4.getPatientId(), "chk", calMinus15.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, true));
		CheckIn c37 = symptomCheckerService.addCheckIn(new CheckIn(pat4, pat4.getPatientId(), "chk", calMinus20.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, true));

		//patient5 has moderate pain issues for 12 hours
		CheckIn c41 = symptomCheckerService.addCheckIn(new CheckIn(pat5, pat5.getPatientId(), "chk", cal1.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, false));
		CheckIn c42 = symptomCheckerService.addCheckIn(new CheckIn(pat5, pat5.getPatientId(), "chk", calMinus3.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, false));
		CheckIn c43 = symptomCheckerService.addCheckIn(new CheckIn(pat5, pat5.getPatientId(), "chk", calMinus6.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, false));
		CheckIn c44 = symptomCheckerService.addCheckIn(new CheckIn(pat5, pat5.getPatientId(), "chk", calMinus9.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, false));
		CheckIn c45 = symptomCheckerService.addCheckIn(new CheckIn(pat5, pat5.getPatientId(), "chk", calMinus12.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, false));
		CheckIn c46 = symptomCheckerService.addCheckIn(new CheckIn(pat5, pat5.getPatientId(), "chk", calMinus15.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.SOME, false));
		CheckIn c47 = symptomCheckerService.addCheckIn(new CheckIn(pat5, pat5.getPatientId(), "chk", calMinus20.getTimeInMillis(), Pain.MODERATE, EatingAffect.SOME, false));

		//patient6 has eating issues for 9 hours
		CheckIn c51 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", cal1.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c52 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus3.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c53 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus6.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c54 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus9.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));
		CheckIn c55 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus12.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));
		CheckIn c56 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus15.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.NONE, false));
		CheckIn c57 = symptomCheckerService.addCheckIn(new CheckIn(pat6, pat6.getPatientId(), "chk", calMinus20.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.CANT_EAT, false));

		//patient7 has severe pain issues for 9 hours
		CheckIn c61 = symptomCheckerService.addCheckIn(new CheckIn(pat7, pat7.getPatientId(), "chk", cal1.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c62 = symptomCheckerService.addCheckIn(new CheckIn(pat7, pat7.getPatientId(), "chk", calMinus3.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c63 = symptomCheckerService.addCheckIn(new CheckIn(pat7, pat7.getPatientId(), "chk", calMinus6.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c64 = symptomCheckerService.addCheckIn(new CheckIn(pat7, pat7.getPatientId(), "chk", calMinus9.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c65 = symptomCheckerService.addCheckIn(new CheckIn(pat7, pat7.getPatientId(), "chk", calMinus12.getTimeInMillis(), Pain.WELL_CONTROLLED, EatingAffect.SOME, false));
		CheckIn c66 = symptomCheckerService.addCheckIn(new CheckIn(pat7, pat7.getPatientId(), "chk", calMinus15.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));
		CheckIn c67 = symptomCheckerService.addCheckIn(new CheckIn(pat7, pat7.getPatientId(), "chk", calMinus20.getTimeInMillis(), Pain.SEVERE, EatingAffect.SOME, false));

		//patient 8 has no checkins
		
		Collection<CheckIn> ci2s = symptomCheckerService.getCheckInList();

		Calendar calMinus48 = Calendar.getInstance();
		calMinus48.add(Calendar.HOUR, -48);

		
		Collection<CheckIn> ci3s = symptomCheckerService.findCheckInByCiPatientId(dpp4.getPatientId(),calMinus48.getTimeInMillis());
		List<Pain> painList = Arrays.asList(Pain.MODERATE,Pain.SEVERE);
		Collection<CheckIn> ci4s = symptomCheckerService.findCheckInByCiPatientIdPainType(pat7.getPatientId(),calMinus48.getTimeInMillis(),painList);
		PatientMedication pm1 = symptomCheckerService.addPatientMedication(new PatientMedication(pat1,"Dimethyltryptamine",pat1.getPatientId()));
		//Patient ux5 = symptomCheckerService.updatePatientReportStatus(pat3.getPatientId());
		Collection<PatientMedication> pm1s = symptomCheckerService.getPatientMedicationList();
		Collection<PatientMedication> pm2s = symptomCheckerService.findPatientMedicationByPmPatientId(dpp4.getPatientId());
		CheckInMedication cim1 = symptomCheckerService.addCheckInMedication(new CheckInMedication(c1,pm1,"desc",true,datNow.getTime(),c1.getCheckInId(),pm1.getPatientMedicationId()));
		Collection<CheckInMedication> cim1s = symptomCheckerService.getCheckInMedicationList();
		
		Collection<CheckInMedication> cim2s = symptomCheckerService.findCheckInMedicationByCimCheckInId(c1.getCheckInId());
		Doctor dxxx1 = symptomCheckerService.getDoctorById(doc1.getDoctorId());
		Collection<Patient> pqrs =symptomCheckerService.findPatientByLastNameAndDoctorId("Tipton", doc2.getDoctorId());
		r = symptomCheckerService.logout();
		Assert.assertEquals(200, r.getStatus());
		int i = 0;
	}

}

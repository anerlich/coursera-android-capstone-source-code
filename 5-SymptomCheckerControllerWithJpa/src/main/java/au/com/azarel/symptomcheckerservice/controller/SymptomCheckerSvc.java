package au.com.azarel.symptomcheckerservice.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Sort;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;
import au.com.azarel.symptomcheckerservice.client.SymptomCheckerSvcApi;
//import au.com.azarel.symptomcheckerservice.repository.Video;
import au.com.azarel.symptomcheckerservice.repository.*;

import com.google.common.collect.Lists;

/**
 * Adapted from VideoSvc in Mobile Cloud course Controller with JPA example
 * 
 * @author jules
 *
 */

@Controller
public class SymptomCheckerSvc implements SymptomCheckerSvcApi {
	
	//
	@Autowired
	private DoctorRepository doctors;
	@Autowired
	private PatientRepository patients;
	@Autowired
	private DoctorPatientRepository doctorPatients;
	@Autowired
	private CheckInRepository checkIns;
	@Autowired
	private PatientMedicationRepository patientMedications;
	@Autowired
	private CheckInMedicationRepository checkInMedications;
	@Autowired
	private UserRepository users;

	//
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public Response login(@RequestParam("username") String username, @RequestParam("password") String password) {
	return null;
	}

	@RequestMapping(value="/logout", method=RequestMethod.POST)
	public Response logout() {
		return null;
		
	}

	@RequestMapping(value="/doctor", method=RequestMethod.POST)
	public @ResponseBody Doctor addDoctor(@RequestBody Doctor d){
		doctors.save(d);
		 return d;
	}

	@RequestMapping(value="/patient", method=RequestMethod.POST)
	public @ResponseBody Patient addPatient(@RequestBody Patient p){
		patients.save(p);
		 return p;
	}

	@RequestMapping(value="/doctorpatient", method=RequestMethod.POST)
	public @ResponseBody DoctorPatient addDoctorPatient(@RequestBody DoctorPatient dp){
		doctorPatients.save(dp);
		 return dp;
	}

	@RequestMapping(value="/checkin", method=RequestMethod.POST)
	public @ResponseBody CheckIn addCheckIn(@RequestBody CheckIn dp){
		checkIns.save(dp);
		 return dp;
	}

	@RequestMapping(value="/patientmedication", method=RequestMethod.POST)
	public @ResponseBody PatientMedication addPatientMedication(@RequestBody PatientMedication pm){
		patientMedications.save(pm);
		 return pm;
	}

	@RequestMapping(value="/checkinmedication", method=RequestMethod.POST)
	public @ResponseBody CheckInMedication addCheckInMedication(@RequestBody CheckInMedication cim){
		checkInMedications.save(cim);
		 return cim;
	}

	@RequestMapping(value="/user", method=RequestMethod.POST)
	public @ResponseBody User addUser(@RequestBody User u){
		users.save(u);
		 return u;
	}


	@RequestMapping(value="/doctor", method=RequestMethod.GET)
	public @ResponseBody Collection<Doctor> getDoctorList(){
		return Lists.newArrayList(doctors.findAll());
	}

	@RequestMapping(value="/patient", method=RequestMethod.GET)
	public @ResponseBody Collection<Patient> getPatientList(){
		return Lists.newArrayList(patients.findAll());
	}

	@RequestMapping(value="/doctorpatient", method=RequestMethod.GET)
	public @ResponseBody Collection<DoctorPatient> getDoctorPatientList(){
		return Lists.newArrayList(doctorPatients.findAll());
	}

	@RequestMapping(value="/checkin", method=RequestMethod.GET)
	public @ResponseBody Collection<CheckIn> getCheckInList(){
		return Lists.newArrayList(checkIns.findAll());
	}

	@RequestMapping(value="/patientmedication", method=RequestMethod.GET)
	public @ResponseBody Collection<PatientMedication> getPatientMedicationList() {
		return Lists.newArrayList(patientMedications.findAll());
	}

	@RequestMapping(value="/checkinmedication", method=RequestMethod.GET)
	public @ResponseBody Collection<CheckInMedication> getCheckInMedicationList() {
		return Lists.newArrayList(checkInMedications.findAll());
	}

	@RequestMapping(value="/user", method=RequestMethod.GET)
	public @ResponseBody Collection<User> getUserList() {
		return Lists.newArrayList(users.findAll());
	}
	
	@RequestMapping(value="/user/{id}", method=RequestMethod.GET)
	public @ResponseBody User getUserById(@PathVariable("id") long id) {
		return users.findOne(id);
	}

	@RequestMapping(value="/doctor/{id}", method=RequestMethod.GET)
	public @ResponseBody Doctor getDoctorById(@PathVariable("id") long id) {
		return doctors.findOne(id);
	}

	@RequestMapping(value="/patient/{id}", method=RequestMethod.GET)
	public @ResponseBody Patient getPatientById(@PathVariable("id") long id) {
		return patients.findOne(id);
	}
	@RequestMapping(value = "/patient/findPatientByLastNameAndDoctorId", method=RequestMethod.GET)
	public @ResponseBody Collection<Patient> findPatientByLastNameAndDoctorId(
			@RequestParam("lastName") String lastName,
			@RequestParam("doctorId") long doctorId) {
		ArrayList<DoctorPatient> dps = new ArrayList(doctorPatients.findDoctorPatientByDpDoctorId(doctorId));
		ArrayList<Long> pIds = new ArrayList();
		for(DoctorPatient dp : dps){
			pIds.add(dp.getDpPatientId());
		}
		Collection<Patient> ps = patients.findPatientByLastNameAndList(lastName, pIds);
		return ps;
	}

	// this checks each patient's checkins and determines whether they have pain or eating
	// issues which require their doctor to be notified
	@RequestMapping(value="/patient/{id}/updatePatientReportStatus", method=RequestMethod.PATCH)
	public @ResponseBody Patient updatePatientReportStatus(@PathVariable("id") long id) {
		boolean bCriticalIssueFound = false;
		Patient p = patients.findOne(id);
		
		Calendar calMinus12 = Calendar.getInstance();
		calMinus12.add(Calendar.HOUR, -12);
		Calendar calMinus16 = Calendar.getInstance();
		calMinus16.add(Calendar.HOUR, -16);
		
		// find all checkins for this patient in last 16 hours
		Collection<CheckIn> ciAllMinus16 = findCheckInByCiPatientId(id, calMinus16.getTimeInMillis());
		
		if (ciAllMinus16.size() == 0){
			//no checkins in last 16 hours
			bCriticalIssueFound = false;
		} else {
			//find how many checkins in last 16 hours indicate moderate or severe pain
			Collection<CheckIn> ciSevereModeratePainMinus16 = 
					findCheckInByCiPatientIdPainType(id, calMinus16.getTimeInMillis(), 
							Arrays.asList(Pain.SEVERE, Pain.MODERATE));
			if (ciAllMinus16.size() == ciSevereModeratePainMinus16.size()){
				// if all the checkins in past 16 hours have moderate or severe pain,
				// Patent has had severe or moderate pain for last 16 hours
				bCriticalIssueFound = true;
			} else {
				
				// find all checkins for this patient in last 12 hours
				Collection<CheckIn> ciAllMinus12 = findCheckInByCiPatientId(id, calMinus12.getTimeInMillis());
				if (ciAllMinus12.size() == 0){
					//no checkins in last 12 hours
					bCriticalIssueFound = false;
				} else {	
					//find how many checkins in last 12 hours indicate severe pain
				Collection<CheckIn> ciSeverePainMinus12 = 
						findCheckInByCiPatientIdPainType(id, calMinus12.getTimeInMillis(), 
								Arrays.asList(Pain.SEVERE));
				if (ciAllMinus12.size() == ciSeverePainMinus12.size()){
					// if all the checkins in past 12 hours have severe pain,
					// Patent has had severe pain for last 12 hours
					bCriticalIssueFound = true;
				} else {
						// find checkins in last 12 houes where patient cannot eat
						Collection<CheckIn> ciCantEatMinus12 = 
								findCheckInByCiPatientIdEatingAffectType(id, calMinus12.getTimeInMillis(), 
										Arrays.asList(EatingAffect.CANT_EAT));
						if (ciAllMinus12.size() == ciCantEatMinus12.size()){
							// if all checkins are "can't eat" check ins,
							//Patient has been unable to eat for 12 hours
							bCriticalIssueFound = true;
						}
					}
				}
			}
		}
		p.setHasCriticalIssue(bCriticalIssueFound);
		p.setIssueReported(false);
		p = patients.save(p);
		return p;		
	}


	@RequestMapping(value="doctorpatient/findDoctorPatientByDpDoctorId", method=RequestMethod.GET)
	public @ResponseBody Collection<DoctorPatient> findDoctorPatientByDpDoctorId(
			@RequestParam("dpDoctorId") long dpDoctorId
	){
		return doctorPatients.findDoctorPatientByDpDoctorId(dpDoctorId);
	}
/*	@RequestMapping(value="doctorpatient/findByPatientId", method=RequestMethod.GET)
	public @ResponseBody Collection<DoctorPatient> findByPatientId(
			// Tell Spring to use the "title" parameter in the HTTP request's query
			// string as the value for the title method parameter
			@RequestParam("patientId") long patientId
	){
		return doctorPatients.findByPatientId(patientId);
	}
*/

	@RequestMapping(value="checkin/findCheckInByCiPatientId", method=RequestMethod.GET)
	public @ResponseBody Collection<CheckIn> findCheckInByCiPatientId(
			@RequestParam("ciPatientId") long ciPatientId,
			@RequestParam("checkInTime") long checkInTime
	){
		return checkIns.findCheckInByCiPatientId(ciPatientId, checkInTime);
	}

	// this allow a list of pain types (e.g. MODERATE and SEVERE) to be passed in as a parameter
	// for use with a JQPL IN clause
	@RequestMapping(value="checkin/findCheckInByCiPatientIdPainType", method=RequestMethod.GET)
	public @ResponseBody Collection<CheckIn> findCheckInByCiPatientIdPainType(
			@RequestParam("ciPatientId") long ciPatientId,
			@RequestParam("checkInTime") long checkInTime,
			@RequestParam("pain") List<Pain> pain
	){
		return checkIns.findCheckInByCiPatientIdPainType(ciPatientId, checkInTime, pain);
	}

	// this allow a list of eating affects (e.g. NONE or SOME) to be passed in as a parameter
	// for use with a JQPL IN clause
	@RequestMapping(value="checkin/findCheckInByCiPatientIdEatingAffectType", method=RequestMethod.GET)
	public @ResponseBody Collection<CheckIn> findCheckInByCiPatientIdEatingAffectType(
			@RequestParam("ciPatientId") long ciPatientId,
			@RequestParam("checkInTime") long checkInTime,
			@RequestParam("eatingAffect") List<EatingAffect> eatingAffect
	){
		return checkIns.findCheckInByCiPatientIdEatingAffectType(ciPatientId, checkInTime, eatingAffect);
	}

	@RequestMapping(value="patientmedication/findPatientMedicationByPmPatientId", method=RequestMethod.GET)
	public @ResponseBody Collection<PatientMedication> findPatientMedicationByPmPatientId(
			@RequestParam("pmPatientId") long pmPatientId
	){
		return patientMedications.findPatientMedicationByPmPatientId(pmPatientId);
	}

	@RequestMapping(value="checkinmedication/findCheckInMedicationByCimCheckInId", method=RequestMethod.GET)
	public @ResponseBody Collection<CheckInMedication> findCheckInMedicationByCimCheckInId(
			// Tell Spring to use the "title" parameter in the HTTP request's query
			// string as the value for the title method parameter
			@RequestParam("cimCheckInId") long cimCheckInId
	){
		return checkInMedications.findCheckInMedicationByCimCheckInId(cimCheckInId);
	}

	@RequestMapping(value="/user/findUserByUserName", method=RequestMethod.GET)
	public @ResponseBody Collection<User> findUserByUserName(
			// Tell Spring to use the "title" parameter in the HTTP request's query
			// string as the value for the title method parameter
			@RequestParam("userName") String userName
	){
		return users.findUserByUserName(userName);
	}

	@RequestMapping(value="/doctor/findDoctorByUserId", method=RequestMethod.GET)
	public @ResponseBody Collection<Doctor> findDoctorByUserId(
			// Tell Spring to use the "title" parameter in the HTTP request's query
			// string as the value for the title method parameter
			@RequestParam("userId") long userId
	){
		return doctors.findDoctorByUserId(userId);
	}

	@RequestMapping(value="/patient/findPatientByUserId", method=RequestMethod.GET)
	public @ResponseBody Collection<Patient> findPatientByUserId(
			// Tell Spring to use the "title" parameter in the HTTP request's query
			// string as the value for the title method parameter
			@RequestParam("userId") long userId
	){
		return patients.findPatientByUserId(userId);
	}


}

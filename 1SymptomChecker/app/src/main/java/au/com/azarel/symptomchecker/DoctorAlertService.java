package au.com.azarel.symptomchecker;

import java.util.ArrayList;
import java.util.Calendar;

import org.magnum.videoup.client.unsafe.EasyHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import au.com.azarel.symptomcheckerservice.client.SymptomCheckerSvcApi;
import au.com.azarel.symptomcheckerservice.repository.CheckIn;
import au.com.azarel.symptomcheckerservice.repository.Doctor;
import au.com.azarel.symptomcheckerservice.repository.DoctorPatient;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.User;

public class DoctorAlertService extends Service {
	// Inspect patient check in data on server and alert doctor via notification if there
	// are patients with pain or eating problems 

	String mServerPrefs;
	User mLoginUser;
	
	String mUserName = "admin";
	// in another life, would probably encrypt password and store in shared preferences
	String mPassword = "changeit";
	Doctor mDoctor;
	Patient mPatient;
	private SymptomCheckerSvcApi mSymptomCheckerService;
	private boolean mbLoginOk;
	private int mIntIntervalMins;
	private long mIntDoctorId;
	private boolean mbCriticalPatientFound;
	
	
	public DoctorAlertService() {
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		 mIntIntervalMins = preferences.getInt("doctorIntervalMins",15);
		 mServerPrefs = preferences.getString("serverIP", StartActivity.SYMPTOM_CHECKER_SERVER_DEFAULT_URL);
		 mIntDoctorId = preferences.getLong("doctorId", 0);
		 
		 if (mIntDoctorId <= 0) {
			 //DoctorID not set by user, forget about checking patient status or supplying notifications
			 stopSelf();
		 }
		 
		 getCheckInInfoFromServer();

		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public boolean getCheckInInfoFromServer() {
		  mSymptomCheckerService = new RestAdapter.Builder()
			.setClient(new ApacheClient(new EasyHttpClient()))
			.setErrorHandler(new MyRetrofitErrorHandler())
			.setEndpoint(mServerPrefs).setLogLevel(LogLevel.FULL).build()
			.create(SymptomCheckerSvcApi.class);
			Thread thread = new Thread()
			{
			    @Override
			    public void run() {
			    	try{
			    		mbLoginOk = true;
			          mSymptomCheckerService.login(mUserName, mPassword);
			    	} catch(Exception e){
			    		mbLoginOk = false;
			    		return;
			        } 
			    	
			    	// Have to get the docto record from server again 
			    	//otherwise an "unsaved transient instance" 
			    	// error occurs
			    	// set patient reference in checkin to newly retrieved value
			    	mbCriticalPatientFound = false;
			    	ArrayList<String> strCritical = new ArrayList<String>();
			    	mDoctor = mSymptomCheckerService.getDoctorById(mIntDoctorId);
			    	ArrayList<DoctorPatient> docpatients = new ArrayList<DoctorPatient>(mSymptomCheckerService.findDoctorPatientByDpDoctorId(mIntDoctorId));
			    	 for (DoctorPatient docpat :docpatients) {
			    		 // check pain and eating status for each patient
			    		 Patient pat = mSymptomCheckerService.UpdatePatientReportStatus(docpat.getDpPatientId());
			    		 if (pat.isHasCriticalIssue() && !(pat.isIssueReported())) {
			    			 //report those with critical issues
			    			 mbCriticalPatientFound = true;
			    			 strCritical.add(pat.getFirstName() + " " + pat.getLastName());
			    		 }
			    	 }
			    	mSymptomCheckerService.logout();
			    	
			    	// if any patients have a reportable issue, broadcast to DoctorAlertReceiver
			    	// to arrange a notification for the doctor
			    	if (mbCriticalPatientFound) {
			    		String strOut="";
			    		boolean bFirst = true;
			    		for (String str: strCritical) {
			    			strOut += (bFirst ? "" : ", ") + str;
			    			bFirst = false;
			    		}
			   		 Intent intentBroadcast = new Intent(DoctorAlertService.this, DoctorAlertReceiver.class);
					 intentBroadcast.putExtra("doctor", mDoctor);
					 intentBroadcast.putExtra("criticalPatients", strOut);
					 sendBroadcast(intentBroadcast);

			    	}
			    }			    
			};
			thread.start();
			
			try{
				thread.join();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			return mbLoginOk;
		}

}

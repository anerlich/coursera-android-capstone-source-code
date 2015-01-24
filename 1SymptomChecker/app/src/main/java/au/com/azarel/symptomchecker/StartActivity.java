package au.com.azarel.symptomchecker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.magnum.videoup.client.unsafe.EasyHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import au.com.azarel.symptomcheckerservice.client.SymptomCheckerSvcApi;
import au.com.azarel.symptomcheckerservice.repository.CheckIn;
import au.com.azarel.symptomcheckerservice.repository.CheckInMedication;
import au.com.azarel.symptomcheckerservice.repository.Doctor;
import au.com.azarel.symptomcheckerservice.repository.DoctorPatient;
import au.com.azarel.symptomcheckerservice.repository.EatingAffect;
import au.com.azarel.symptomcheckerservice.repository.Pain;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.PatientMedication;
import au.com.azarel.symptomcheckerservice.repository.User;
import au.com.azarel.symptomcheckerservice.repository.UserType;

public class StartActivity extends ActionBarActivity implements OnGesturePerformedListener {
	// start screen to allow user to log in 
	// also retrieves relevant info from server after a successful login
	public static final String SYMPTOM_CHECKER_SERVER_DEFAULT_URL = "https://192.168.0.3:8443";
	  private GestureLibrary gestureLib;
	  private Menu mMenu;
	  boolean mbLoginOk;
		private SymptomCheckerSvcApi mSymptomCheckerService;
		private String mUserName;
		private String mPassword;
		private User mLoginUser;
		private Patient mPatient;
		private Doctor mDoctor;
		private Collection<DoctorPatient> mDoctorPatients;
		private ArrayList<DoctorPatient> mDoctorPatientArrayList;
		private ArrayList<Patient> mPatientArrayList;
		
		private Collection<PatientMedication> mPatientMedications;
		private String mServerPrefs;
		final ReentrantLock lock = new ReentrantLock();
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		// Gesture panel
		GestureOverlayView  gov = (GestureOverlayView)findViewById(R.id.gestureOverlayView1);
	    gov.addOnGesturePerformedListener(this);
	    // load infinity gesture to which to compare user's effort
	    gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
	    if (!gestureLib.load()) {
	      finish();
	    }
	    // hide gesture panel until after successful login
	    gov.setVisibility(View.INVISIBLE);
	    gov.setEnabled(false);
		 TextView tvDrawGesture = (TextView)findViewById(R.id.tvDrawGesture);
		 tvDrawGesture.setVisibility(View.INVISIBLE);
		
		 Button btnLogin = (Button)findViewById(R.id.btnLogin);
		 final EditText etUserName = (EditText)findViewById(R.id.etUserName);
		 final EditText etPassword = (EditText)findViewById(R.id.etPassword);

		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			mServerPrefs = preferences.getString("serverIP",SYMPTOM_CHECKER_SERVER_DEFAULT_URL);
			final EditText etServer = (EditText)findViewById(R.id.etServer);
			etServer.setText(mServerPrefs);

			// Login button
			
			btnLogin.setOnClickListener(new OnClickListener(){
			 @TargetApi(Build.VERSION_CODES.GINGERBREAD)
			public void onClick(View v){
				 mServerPrefs = etServer.getText().toString();
				 boolean bResult =  TryLogin(etUserName.getText().toString(),etPassword.getText().toString());
				 String strToast;
				 if (bResult){
					 	// login was successful
					 	mUserName = etUserName.getText().toString();
					 	mPassword = etPassword.getText().toString();
					 	
					 	//save or resave server string
						SharedPreferences prefsOnClick = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
				        SharedPreferences.Editor editor = prefsOnClick.edit();
				        editor.putString("serverIP", etServer.getText().toString());
				        editor.apply();

					 	if (mLoginUser.getUserType()==UserType.DOCTOR) {
					 		// doctor has logged in
					 		strToast = "DOCTOR " + mLoginUser.getLastName();
					 		//save doctorId in Shared Preferences so we can use it in DoctorAlertService
					 		//used to check for and provide doctor notifications
							prefsOnClick = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
					        editor = prefsOnClick.edit();
					        editor.putLong("doctorId", mDoctor.getDoctorId());
					        editor.apply();
					        
					 	} else {
					 		// a patient has logged in
					 		strToast = mLoginUser.getFirstName() + " " + mLoginUser.getLastName();
					 	}					 		
					 	// set up Toast message to show user they have been authenticated and recognized
				        Toast.makeText(getApplicationContext(), "Login Successful, " + strToast, Toast.LENGTH_SHORT)
			            .show();
				        
				        // disable login controls, enable and make visible gesture view
						 etUserName.setEnabled(false);
						 etPassword.setEnabled(false);
						 etServer.setEnabled(false);
						 v.setEnabled(false); // disable the button itself
						 TextView tvDrawGesture = (TextView)findViewById(R.id.tvDrawGesture);
						 tvDrawGesture.setVisibility(View.VISIBLE);
						 GestureOverlayView gov1 = (GestureOverlayView)findViewById(R.id.gestureOverlayView1);
						 gov1.setEnabled(true);
						   gov1.setVisibility(View.VISIBLE);
			 
				 } else {
					 	// login failed
				        Toast.makeText(getApplicationContext(), "Login Failed - check credentials and connectivity", Toast.LENGTH_LONG)
			            .show();					 
				 }
			 }
		 });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		mMenu = menu;
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_new_login) {
			// reenable controls to allow new login
			 Button btnLogin = (Button)findViewById(R.id.btnLogin);
			 EditText etUserName = (EditText)findViewById(R.id.etUserName);
			 EditText etPassword = (EditText)findViewById(R.id.etPassword);
			 EditText etServer = (EditText)findViewById(R.id.etServer);
			 etUserName.setEnabled(true);
			 etPassword.setEnabled(true);
			 etServer.setEnabled(true);
			 btnLogin.setEnabled(true);
			 TextView tvDrawGesture = (TextView)findViewById(R.id.tvDrawGesture);
			 tvDrawGesture.setVisibility(View.INVISIBLE);
			 GestureOverlayView gov1 = (GestureOverlayView)findViewById(R.id.gestureOverlayView1);
			 gov1.setEnabled(false);
			   gov1.setVisibility(View.INVISIBLE);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	  @Override
	  public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		  //checks user's entered gesture against stored gesture
	    ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
	    for (Prediction prediction : predictions) {
	      if (prediction.score > 1.0) {
	    	StringBuilder sb = new StringBuilder();
	    	sb.append("Nice ");
	    	sb.append(prediction.name);
	    	if (mLoginUser.getUserType() == UserType.DOCTOR) {
	    		sb.append("! Go on now and review your patients' statuses.");
	    	} else {
	    		sb.append("! Go on now and Check In.");
	    	}
	        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG)
	            .show();
	    	if (mLoginUser.getUserType() == UserType.DOCTOR) {
	    		// doctor, go to list of doctor's patients
				Intent doctorIntent = new Intent(this,DoctorPatientListActivity.class);
				doctorIntent.putExtra("serverPrefs", mServerPrefs);	// server connection string
				doctorIntent.putExtra("userName", mUserName);
				doctorIntent.putExtra("password", mPassword);
				doctorIntent.putExtra("user", mLoginUser);
				doctorIntent.putExtra("doctor", mDoctor);
				doctorIntent.putParcelableArrayListExtra("doctorPatients", (ArrayList<? extends Parcelable>) mDoctorPatientArrayList);
				doctorIntent.putParcelableArrayListExtra("patients", (ArrayList<? extends Parcelable>) mPatientArrayList);
				startActivity(doctorIntent);	    		
	    	} else {
	    		// patient, go to check in
				Intent checkInIntent = new Intent(this,CheckInActivity.class);
				checkInIntent.putExtra("serverPrefs", mServerPrefs);	// server connection string
				checkInIntent.putExtra("userName", mUserName);
				checkInIntent.putExtra("password", mPassword);
				checkInIntent.putExtra("user", mLoginUser);
				checkInIntent.putExtra("patient", mPatient);
				ArrayList<PatientMedication> patMedArrayList = new ArrayList<PatientMedication>(mPatientMedications);
				checkInIntent.putParcelableArrayListExtra("patientMedications", (ArrayList<? extends Parcelable>) patMedArrayList);
				startActivity(checkInIntent);
	    	}
	      }
	    }
	  }
	  
	  private boolean TryLogin(final String userName, final String password){
		  mSymptomCheckerService = new RestAdapter.Builder()
			.setClient(new ApacheClient(new EasyHttpClient()))
			.setErrorHandler(new MyRetrofitErrorHandler())
			.setEndpoint(mServerPrefs).setLogLevel(LogLevel.FULL).build()
			.create(SymptomCheckerSvcApi.class);
			Thread thread = new Thread()
			{
			    @Override
			    public void run() {
			    	// attempt login using suppled username and password
			    	try{
			    		mbLoginOk = true;
			          mSymptomCheckerService.login(userName, password);
			    	} catch(Exception e){
			    		// login failed
			    		mbLoginOk = false;
			    		return;
			        } 
			    	
			    	// login succeeded
			        Collection<User> users = mSymptomCheckerService.findUserByUserName(userName);
			        User[] aUsers = users.toArray(new User[0]);
			        mLoginUser = aUsers[0];
			        if (mLoginUser.getUserType() == UserType.DOCTOR) {
			        	// doctor - get Doctor, related Patients info
				        Collection<Doctor> doctors = 
				        		mSymptomCheckerService.findDoctorByUserId(mLoginUser.getUserId());
				        Doctor[] aDoctors = doctors.toArray(new Doctor[0]);
				        mDoctor = aDoctors[0];
				        mDoctorPatients = mSymptomCheckerService.findDoctorPatientByDpDoctorId(mDoctor.getDoctorId());
				        mDoctorPatientArrayList = new ArrayList<DoctorPatient>(mDoctorPatients);
				        mPatientArrayList = new ArrayList<Patient>();
				        for (int iDP=0; iDP < mDoctorPatientArrayList.size(); iDP++){
				        	Patient p = mSymptomCheckerService.getPatientById(mDoctorPatientArrayList.get(iDP).getDpPatientId());
				        	mDoctorPatientArrayList.get(iDP).setPatient(p);
				        	mPatientArrayList.add(iDP, p);
				        }
			        	
			        } else {
			        	// patient, get Patient and PatientMedication info
					        Collection<Patient> patients = 
					        		mSymptomCheckerService.findPatientByUserId(mLoginUser.getUserId());
					        Patient[] aPatients = patients.toArray(new Patient[0]);
					        mPatient = aPatients[0];
					        mPatientMedications = 
			        		mSymptomCheckerService.findPatientMedicationByPmPatientId(mPatient.getPatientId());
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

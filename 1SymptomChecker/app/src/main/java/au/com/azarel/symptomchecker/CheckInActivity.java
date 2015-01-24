package au.com.azarel.symptomchecker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.magnum.videoup.client.unsafe.EasyHttpClient;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RequestInterceptor.RequestFacade;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.util.Base64;
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

public class CheckInActivity extends ActionBarActivity {
	
	// This activity is used by the patient to Check In

	private SymptomCheckerSvcApi mSymptomCheckerService;
	private String mServerPrefs;
	private User mLoginUser;
	private Patient mPatient;
	private String mUserName;
	private String mPassword;
	private Pain mPain;
	private EatingAffect mEatingAffect;
	private boolean mbTookMedication;
	private boolean mbLoginOk;
	private CheckIn mCheckIn;
	private ArrayList<PatientMedication> mPatientMedications;
	private ArrayList<CheckInMedication> mCheckInMedications;
	private CheckInMedicationAdapter mCheckInMedicationAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_in);
		
		// obtain user and patient info from intent extras
		Intent intent = getIntent();
		mServerPrefs = intent.getStringExtra("serverPrefs");
		mUserName = intent.getStringExtra("userName");
		mPassword = intent.getStringExtra("password");
		mLoginUser = intent.getParcelableExtra("user");
		mPatient = intent.getParcelableExtra("patient");
		mPatientMedications = intent.getParcelableArrayListExtra("patientMedications");
		
		// set initial values to match defaults in layout XML file
		mPain = Pain.WELL_CONTROLLED;
		mEatingAffect = EatingAffect.NONE;
		mbTookMedication = false;
		
		// initialize Arraylist of CheckInMedications to match that of PatientMedications
		// for this patient
		Date datNow = new Date();
		mCheckInMedications = new ArrayList<CheckInMedication>();
		for (int iPM = 0; iPM < mPatientMedications.size(); iPM++) {
			CheckInMedication checkInMedication = new CheckInMedication(mCheckIn,
					mPatientMedications.get(iPM),"desc not needed",false,datNow.getTime(),0,mPatientMedications.get(iPM).getPatientMedicationId());
			mCheckInMedications.add(iPM, checkInMedication);
		}

		// set up listview for check in medications
		ListView listView;
		listView = (ListView) findViewById(R.id.checkin_medication_list);
		mCheckInMedicationAdapter = new CheckInMedicationAdapter(this, mCheckInMedications);
		listView.setAdapter(mCheckInMedicationAdapter );

		
		// Save button
		Button btnOK = (Button)findViewById(R.id.btnCheckInOK);
		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Set up new Check In
				Date datNow = new Date();
				mCheckIn = new CheckIn(mPatient, mPatient.getPatientId(), "desc not needed",
						datNow.getTime(), mPain, mEatingAffect, mbTookMedication);
				ListView listView1 = (ListView) findViewById(R.id.checkin_medication_list);

		        //BUG This doesn't work unless all ListView rows are visible
				// need to update items in the listAdapter and use getItemAtPosition instead
		        ArrayList<String> timesToSave = new ArrayList<String>();
		        for (int i = 0; i < listView1.getCount(); i++) {
		        	View innerView = listView1.getChildAt(i);
		        	CheckBox ckTookIt = (CheckBox)innerView.findViewById(R.id.ckMedication);
		        	TextView tvDate = (TextView)innerView.findViewById(R.id.tvDate);
		            TextView tvTime = (TextView)innerView.findViewById(R.id.tvTime);
		            mCheckInMedications.get(i).setTookIt(ckTookIt.isChecked());
		            String strDateTimeMed = tvDate.getText() + " " + tvTime.getText() + ":00";
		        	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		        	Date datMed = new Date();
					try {
						datMed = formatter.parse(strDateTimeMed);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//default to current date/time as set before try/catch block, but print message on exception
			        	System.out.println("Parse exception on CheckInMedication date " + strDateTimeMed);
					}
		        	mCheckInMedications.get(i).setTookItTime(datMed.getTime());
		        }

				AddCheckInToServer();
			}		
		});
		
		Button btnCancel = (Button)findViewById(R.id.btnCheckInCancel);
		btnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.check_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings_patient) {
			// take patient to activity to allow reminder times to be set
			Intent timeSettingsIntent = new Intent(CheckInActivity.this,TimeSettingsActivity.class);
			startActivity(timeSettingsIntent);
			return true;
		}
		if (id == R.id.action_details_patient) {
			// show patient details
			Intent detailsIntent = new Intent(CheckInActivity.this, DetailsActivity.class);
			detailsIntent.putExtra("type", "patient");
			detailsIntent.putExtra("patient", mPatient);
			startActivity(detailsIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.rbPainWellControlled:
	            if (checked)
	                mPain = Pain.WELL_CONTROLLED;
	            break;
	        case R.id.rbPainModerate:
	            if (checked)
	                mPain = Pain.MODERATE;
	            break;
	        case R.id.rbPainSevere:
	            if (checked)
	                mPain = Pain.SEVERE;
	            break;
	        case R.id.rbEatingAffectNone:
	            if (checked)
	                mEatingAffect = EatingAffect.NONE;
	            break;
	        case R.id.rbEatingAffectSome:
	            if (checked)
	                mEatingAffect = EatingAffect.SOME;
	            break;
	        case R.id.rbEatingAffectCantEat:
	            if (checked)
	                mEatingAffect = EatingAffect.CANT_EAT;
	            break;
	    }
	}
	
	public void ckMedication_OnClick(View v){
		mbTookMedication = ((CheckBox)v).isChecked();
	}
	
	private void AddCheckInToServer(){
		
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
			    	
			    	// Have to get the patient record from server again 
			    	//otherwise an "unsaved transient instance" 
			    	// error occurs
			    	Collection<Patient> patients = mSymptomCheckerService.findPatientByUserId(mLoginUser.getUserId());
			    	Patient[] aPatients = patients.toArray(new Patient[0]);
			    	// set patient reference in checkin to newly retrieved value
			    	mCheckIn.setPatient(aPatients[0]);
			    	mCheckIn.setCiPatientId(aPatients[0].getPatientId());
			    	mCheckIn = mSymptomCheckerService.addCheckIn(mCheckIn);
			    	// update each CheckInMedication with newly added CheckIn's id value
			    	for (int iCIM = 0; iCIM < mCheckInMedications.size(); iCIM++){
			    		mCheckInMedications.get(iCIM).setCheckIn(mCheckIn);
			    		mCheckInMedications.get(iCIM).setCimCheckInId(mCheckIn.getCheckInId());
			    		CheckInMedication cim = 
			    				mSymptomCheckerService.addCheckInMedication(mCheckInMedications.get(iCIM));
						int idf = 0; // to allow breakpoint
						idf++;
			    	}
			        runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stu							
					        Toast.makeText(CheckInActivity.this, "CheckIn saved", Toast.LENGTH_SHORT)
				            .show();

							finish();
						}
			        	
			        });

			    }			    
			};
			thread.start();
			
	  }
	 
}

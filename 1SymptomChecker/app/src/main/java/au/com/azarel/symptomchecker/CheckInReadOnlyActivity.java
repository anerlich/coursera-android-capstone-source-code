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

public class CheckInReadOnlyActivity extends ActionBarActivity {
	
	// Read only version of check in form
	// used by doctor to view a patient's individual check in

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
		
		// obtain user info from intent extras
		Intent intent = getIntent();
		mServerPrefs = intent.getStringExtra("serverPrefs");
		mUserName = intent.getStringExtra("userName");
		mPassword = intent.getStringExtra("password");
		mLoginUser = intent.getParcelableExtra("user");
		mPatient = intent.getParcelableExtra("patient");
		mCheckIn = intent.getParcelableExtra("checkin");
		//mPatientMedications = intent.getParcelableArrayListExtra("patientMedications");
		
		// set initial values to match defaults in layout XML file
		mPain = mCheckIn.getPain();
		
		switch(mPain) {
		case WELL_CONTROLLED:
			RadioButton rbPainWC = (RadioButton)findViewById(R.id.rbPainWellControlled);
			rbPainWC.setChecked(true);
			break;
		case MODERATE:
			((RadioButton)findViewById(R.id.rbPainModerate)).setChecked(true);
			break;
		case SEVERE:
			((RadioButton)findViewById(R.id.rbPainSevere)).setChecked(true);
			break;		
		}
		
		mEatingAffect = mCheckIn.getEatingAffect();

		switch(mEatingAffect) {
		case NONE:
			RadioButton rbPainWC = (RadioButton)findViewById(R.id.rbEatingAffectNone);
			rbPainWC.setChecked(true);
			break;
		case SOME:
			((RadioButton)findViewById(R.id.rbEatingAffectSome)).setChecked(true);
			break;
		case CANT_EAT:
			((RadioButton)findViewById(R.id.rbEatingAffectCantEat)).setChecked(true);
			break;		
		}
		
mbTookMedication = mCheckIn.getTookMedication();
((CheckBox)findViewById(R.id.ckMedication)).setChecked(mbTookMedication);
		
		// initialize Arraylist of CheckInMedications to match that of PatientMedications
		Date datNow = new Date();
		boolean bResult = getMedicationsFromServer();

		// listView of check in medications, time taken etc.
		ListView listView;
		listView = (ListView) findViewById(R.id.checkin_medication_list);
		listView.setEnabled(false);

		mCheckInMedicationAdapter = new CheckInMedicationReadOnlyAdapter(this, mCheckInMedications);

		listView.setAdapter(mCheckInMedicationAdapter );

		
		// Save button
		Button btnOK = (Button)findViewById(R.id.btnCheckInOK);
		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
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
/*			Intent timeSettingsIntent = new Intent(CheckInReadOnlyActivity.this,TimeSettingsActivity.class);
			startActivity(timeSettingsIntent);
*/			return true;
		}
		if (id == R.id.action_details_patient) {
			Intent detailsIntent = new Intent(CheckInReadOnlyActivity.this, DetailsActivity.class);
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
	
	private boolean getMedicationsFromServer(){
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
			    	Collection<CheckInMedication> cim1s = mSymptomCheckerService.getCheckInMedicationList();
			    	mCheckInMedications = new ArrayList<CheckInMedication>(mSymptomCheckerService.findCheckInMedicationByCimCheckInId(mCheckIn.getCheckInId()));
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

package au.com.azarel.symptomchecker;

import java.util.ArrayList;
import java.util.Collection;

import org.magnum.videoup.client.unsafe.EasyHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import au.com.azarel.symptomcheckerservice.client.SymptomCheckerSvcApi;
import au.com.azarel.symptomcheckerservice.repository.CheckIn;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.PatientMedication;
import au.com.azarel.symptomcheckerservice.repository.User;

public class PatientMedicationListActivity extends ActionBarActivity {
	// allows doctor to view or add to patient's list of medications
	
	String mServerPrefs;
	User mLoginUser;
	String mUserName;
	String mPassword;
	Patient mPatient;
	private SymptomCheckerSvcApi mSymptomCheckerService;
	private boolean mbLoginOk;
	private Collection<PatientMedication> mPatientMedications;
	private ArrayList<PatientMedication> mPatientMedicationArrayList;
	PatientMedicationListAdapter mPatientMedicationListAdapter;
	ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_medication_list);
		Intent intent = getIntent();
		mServerPrefs = intent.getStringExtra("serverPrefs");
		mUserName = intent.getStringExtra("userName");
		mPassword = intent.getStringExtra("password");
		mLoginUser = intent.getParcelableExtra("user");
		mPatient = intent.getParcelableExtra("patient");
		
		// gets an ArrayList of Patient Medications from the server into mPatientMedicationArrayList
		if (!getMedicationInfoFromServer()){
			return;
		}
		
		// set heading text to patient's name
		TextView tvPatientMedicationPatient = (TextView) findViewById(R.id.tvPatientMedicationPatient);
	    tvPatientMedicationPatient.setText(mPatient.getFirstName() + " " + mPatient.getLastName());

		
		// set up listView of medications
	    mListView = (ListView) findViewById(R.id.lvPatientMedicationList);

		mPatientMedicationListAdapter = 
				new PatientMedicationListAdapter(this, mPatientMedicationArrayList);
		mPatientMedicationListAdapter.setUser(mLoginUser);
		mPatientMedicationListAdapter.setPassword(mPassword);
		mPatientMedicationListAdapter.setUserName(mUserName);
		mPatientMedicationListAdapter.setServerPrefs(mServerPrefs);
		mListView.setAdapter(mPatientMedicationListAdapter);

		Button btnAddMedication = (Button) findViewById(R.id.btnPatientMedicationAdd);
		btnAddMedication.setEnabled(false);
		EditText etAddMedication = (EditText)findViewById(R.id.etNewMedication);
		etAddMedication.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// Only enable add button when a string has been entered
				Button btnAddMedication = (Button) findViewById(R.id.btnPatientMedicationAdd);
				int strlen = s.toString().replace(" ", "").length();
				btnAddMedication.setEnabled((strlen == 0)? false: true);
			}
			
			
		});

		btnAddMedication.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText etAddMedication = (EditText)findViewById(R.id.etNewMedication);
				PatientMedication pm = new PatientMedication(mPatient,etAddMedication.getText().toString(),mPatient.getPatientId());
				if(!addNewMedicationToServer(pm)){
			        Toast.makeText(getApplicationContext(), "Unable to add new medication on server", Toast.LENGTH_LONG)
		            .show();
				}
				etAddMedication.setText("");
				v.setEnabled(false);
				mPatientMedicationArrayList.add(pm);
				mPatientMedicationListAdapter.notifyDataSetChanged();
				//line below included because the notifyDataSetChanged does not work
				//I realise it is wasteful as hell on resources, but WTH, it works
				PatientMedicationListActivity.this.recreate();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_medication_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean getMedicationInfoFromServer() {
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
			    	// set patient reference in checkin to newly retrieved value
			    	mPatient = mSymptomCheckerService.getPatientById(mPatient.getPatientId());
			    	// obtain medications from server
			    	mPatientMedicationArrayList =  new ArrayList<PatientMedication>(mSymptomCheckerService.findPatientMedicationByPmPatientId(mPatient.getPatientId()));
			    	mSymptomCheckerService.logout();
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

	public boolean addNewMedicationToServer(final PatientMedication newPatientMedication) {
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
			    	// set patient reference in checkin to newly retrieved value
			    	mPatient = mSymptomCheckerService.getPatientById(mPatient.getPatientId());
			    	newPatientMedication.setPatient(mPatient);
			    	newPatientMedication.setPmPatientId(mPatient.getPatientId());
			    	// add new medication and refresh the list of medications for this patient
			    	PatientMedication pm = mSymptomCheckerService.addPatientMedication(newPatientMedication);
			    	mPatientMedicationArrayList =  new ArrayList<PatientMedication>(mSymptomCheckerService.findPatientMedicationByPmPatientId(mPatient.getPatientId()));
			    	mSymptomCheckerService.logout();
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

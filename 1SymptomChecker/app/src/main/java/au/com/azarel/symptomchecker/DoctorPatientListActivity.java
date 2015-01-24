package au.com.azarel.symptomchecker;

import java.util.ArrayList;
import java.util.Collection;

import org.magnum.videoup.client.unsafe.EasyHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import au.com.azarel.symptomcheckerservice.client.SymptomCheckerSvcApi;
import au.com.azarel.symptomcheckerservice.repository.Doctor;
import au.com.azarel.symptomcheckerservice.repository.DoctorPatient;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.User;
import au.com.azarel.symptomcheckerservice.repository.UserType;

public class DoctorPatientListActivity extends ActionBarActivity {
	// list of a doctor's patients
	// includes a simple server side text search on patient's last name
	boolean mbLoginOk;
	private SymptomCheckerSvcApi mSymptomCheckerService;
	private User mLoginUser;
	private String mUserName;
	private String mPassword;
	private String mServerPrefs;
	private Doctor mDoctor;
	private ArrayList<DoctorPatient> mDoctorPatients;
	private ArrayList<Patient> mPatients;
	private DoctorPatientListAdapter mPatientListAdapter;
	private String mStrSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_patient_list);
		Intent intent = getIntent();
		mServerPrefs = intent.getStringExtra("serverPrefs");
		mUserName = intent.getStringExtra("userName");
		mPassword = intent.getStringExtra("password");
		mLoginUser = intent.getParcelableExtra("user");
		mDoctor = intent.getParcelableExtra("doctor");
		mDoctorPatients = intent.getParcelableArrayListExtra("doctorPatients");
		mPatients = intent.getParcelableArrayListExtra("patients");

		// listView construction
		ListView listView;
		listView = (ListView) findViewById(R.id.lvPatientList);

		mPatientListAdapter = new DoctorPatientListAdapter(this, mPatients);
		mPatientListAdapter.setUser(mLoginUser);
		mPatientListAdapter.setPassword(mPassword);
		mPatientListAdapter.setUserName(mUserName);
		mPatientListAdapter.setServerPrefs(mServerPrefs);
		listView.setAdapter(mPatientListAdapter);
		
		// text search implemented here
		EditText etSearch = (EditText)findViewById(R.id.etSearch);
		etSearch.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		            NameSearchOnServer(v.getText().toString());

		            return true;
		        }

				return false;
			}
			
		});
		
		Button btnOK = (Button)findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListener(){

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
		getMenuInflater().inflate(R.menu.doctor_patient_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// doctor settings - interval at which to poll server
			Intent doctorSettingsIntent = new Intent(this,DoctorSettingsActivity.class);			
			startActivity(doctorSettingsIntent);
			return true;
		}
		if (id == R.id.action_doctor_detail) {
			// display doctor's details
			Intent detailsIntent = new Intent(this,DetailsActivity.class);
			detailsIntent.putExtra("type", "doctor");
			detailsIntent.putExtra("doctor", mDoctor);
			startActivity(detailsIntent);
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	  private void NameSearchOnServer(String strSearch){
		  mStrSearch = strSearch.trim();
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
			        if (mStrSearch == "") {
			        	// empty search string, show all patients
			        	mDoctorPatients = new ArrayList<DoctorPatient>(mSymptomCheckerService.findDoctorPatientByDpDoctorId(mDoctor.getDoctorId()));
				        // have to use clear() and add() on mPatients so the listadapter/BaseAdapter
			        	//recognises the underlying dataset has changed
			        	mPatients.clear();
				        for (int iDP=0; iDP < mDoctorPatients.size(); iDP++){
				        	Patient p = mSymptomCheckerService.getPatientById(mDoctorPatients.get(iDP).getDpPatientId());
				        	mDoctorPatients.get(iDP).setPatient(p);
				        	mPatients.add(iDP, p);
				        }
			        } else {
			        	// filter patient list on search string
				        // have to use clear() and add() on mPatients so the listadapter/BaseAdapter
			        	//recognises the underlying dataset has changed
			  		  	mPatients.clear();
			        	ArrayList<Patient> tempPatients = new ArrayList<Patient>(mSymptomCheckerService.findPatientByLastNameAndDoctorId(mStrSearch, mDoctor.getDoctorId()));
			        	for (Patient p: tempPatients) {
			        		mPatients.add(p);
			        	}
			        }
			        runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub							
							mPatientListAdapter.notifyDataSetChanged();
						}
			        	
			        });
			    }
			};
			thread.start();
	  }

}

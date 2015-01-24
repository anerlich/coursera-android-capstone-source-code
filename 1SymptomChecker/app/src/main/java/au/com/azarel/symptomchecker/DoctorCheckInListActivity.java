package au.com.azarel.symptomchecker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.magnum.videoup.client.unsafe.EasyHttpClient;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import au.com.azarel.symptomcheckerservice.client.SymptomCheckerSvcApi;
import au.com.azarel.symptomcheckerservice.repository.CheckIn;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.User;

public class DoctorCheckInListActivity extends ActionBarActivity {
	// list of a patient's check in history - used by their doctor
	String mServerPrefs;
	User mLoginUser;
	String mUserName;
	String mPassword;
	Patient mPatient;
	private SymptomCheckerSvcApi mSymptomCheckerService;
	private boolean mbLoginOk;
	private ArrayList<CheckIn> mCheckIns;
	private DoctorCheckInListAdapter mCheckInListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_checkin_list);
		Intent intent = getIntent();
		mServerPrefs = intent.getStringExtra("serverPrefs");
		mUserName = intent.getStringExtra("userName");
		mPassword = intent.getStringExtra("password");
		mLoginUser = intent.getParcelableExtra("user");
		mPatient = intent.getParcelableExtra("patient");
		getCheckInInfoFromServer();
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.doctor_checkin_list, menu);
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
	public void getCheckInInfoFromServer() {
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
			    	//find checkins for this patient in last 48 hours
			    	Calendar cal = Calendar.getInstance();
			    	cal.add(Calendar.HOUR, -48);
			    	mCheckIns =  new ArrayList<CheckIn>(mSymptomCheckerService.findCheckInByCiPatientId(mPatient.getPatientId(),cal.getTimeInMillis()));
			    	mSymptomCheckerService.logout();
			    	
			        runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stu							
							TextView tvTitle  = (TextView) findViewById(R.id.tvDoctorCheckInTitle);
							tvTitle.setText(mPatient.getFirstName() + " " + mPatient.getLastName());
							
							//set up listview
							ListView listView = (ListView) findViewById(R.id.lvCheckInList);

							mCheckInListAdapter = new DoctorCheckInListAdapter(DoctorCheckInListActivity.this, mCheckIns);
							mCheckInListAdapter.setUser(mLoginUser);
							mCheckInListAdapter.setPassword(mPassword);
							mCheckInListAdapter.setUserName(mUserName);
							mCheckInListAdapter.setServerPrefs(mServerPrefs);
							mCheckInListAdapter.setmPatient(mPatient);;
							listView.setAdapter(mCheckInListAdapter);
							listView.setClickable(true);
							listView.setFocusable(true);
							listView.setItemsCanFocus(true);
							
							// clicking a a list view item takes you to a detailed read only view of that
							// check in
							listView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
									// TODO Auto-generated method stub
						    		Intent checkInIntent = new Intent(DoctorCheckInListActivity.this,CheckInReadOnlyActivity.class);
						    		checkInIntent.putExtra("serverPrefs", mServerPrefs);
						    		checkInIntent.putExtra("userName", mUserName);
						    		checkInIntent.putExtra("password", mPassword);
						    		checkInIntent.putExtra("user", mLoginUser);
						    		checkInIntent.putExtra("patient", mPatient);
						    		CheckIn ck = mCheckIns.get(position);
						    		checkInIntent.putExtra("checkin", ck);
						    		startActivity(checkInIntent);
						
								}
								
							});
						}
			        	
			        });

			    }			    
			};
			thread.start();
			
		}

}

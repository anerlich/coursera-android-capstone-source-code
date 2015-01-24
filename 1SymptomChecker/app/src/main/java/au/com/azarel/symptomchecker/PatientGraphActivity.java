package au.com.azarel.symptomchecker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.magnum.videoup.client.unsafe.EasyHttpClient;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.ValueDependentColor;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import au.com.azarel.symptomcheckerservice.client.SymptomCheckerSvcApi;
import au.com.azarel.symptomcheckerservice.repository.CheckIn;
import au.com.azarel.symptomcheckerservice.repository.CheckInMedication;
import au.com.azarel.symptomcheckerservice.repository.Patient;
import au.com.azarel.symptomcheckerservice.repository.PatientMedication;
import au.com.azarel.symptomcheckerservice.repository.User;


/*
 * Graphs made with GraphView (http://android-graphview.org/#)
 */

public class PatientGraphActivity extends ActionBarActivity {
	// Graph history of pain and its effect on the patient's ability to eat

	String mServerPrefs;
	User mLoginUser;
	String mUserName;
	String mPassword;
	Patient mPatient;
	private SymptomCheckerSvcApi mSymptomCheckerService;
	private boolean mbLoginOk;
	private Collection<CheckIn> mCheckIns;
	private ArrayList<CheckIn> mCheckInArrayList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_graph);
		Intent intent = getIntent();
		mServerPrefs = intent.getStringExtra("serverPrefs");
		mUserName = intent.getStringExtra("userName");
		mPassword = intent.getStringExtra("password");
		mLoginUser = intent.getParcelableExtra("user");
		mPatient = intent.getParcelableExtra("patient");
		if (!getCheckInInfoFromServer()){
			return;
		}
		
		// Graph history of pain and its effect on the patient's ability to eat
		TextView tvGraphPatientName = (TextView)findViewById(R.id.tvGraphPatientName);
	    tvGraphPatientName.setText(mPatient.getFirstName() + " " + mPatient.getLastName());

	    // give the bars of the graph different colours for the severity of the issue
	    // Blue for Well Controlled pain or no eating affect
	    // Magenta for Moderate Pain or Some eating affect
	    // Red for Severe Pain or Can't Eat
	    
	    GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();
	    seriesStyle.setValueDependentColor(new ValueDependentColor() {
	      @Override
	      public int get(GraphViewDataInterface data) {
		      if (data.getY() <= 1.0) {
			        return Color.BLUE;
			      } else if (data.getY() <= 2.0) {
			        return Color.MAGENTA;
			      } else {
			        return Color.RED;
			      }
	      }
	    });
	    
	    // read the list of checkins for this patient and formulate the series for the
	    // pain and eating affect graphs
	    
		mCheckInArrayList = new ArrayList<CheckIn>(mCheckIns);
		GraphView.GraphViewData[] dataPain = new GraphView.GraphViewData[mCheckInArrayList.size()];
		GraphView.GraphViewData[] dataEatingAffect = new GraphView.GraphViewData[mCheckInArrayList.size()];
		for (int i = 0; i < mCheckInArrayList.size(); i ++)
		{
			dataPain[i] = new GraphView.GraphViewData(i, 
					mCheckInArrayList.get(
							mCheckInArrayList.size() - i - 1).
							getPain().ordinal() + 1.0);
			dataEatingAffect[i] = new GraphView.GraphViewData(i, 
					mCheckInArrayList.get(
							mCheckInArrayList.size() - i - 1).
							getEatingAffect().ordinal() + 1.0);
		}
		
		GraphViewSeries seriesPain = new GraphViewSeries("Pain", seriesStyle, dataPain);
		GraphView graphViewPain = new BarGraphView(
		    this // context
		    , "Pain" // heading
		);

		//labels for pain graph Y axis
		graphViewPain.setCustomLabelFormatter(new CustomLabelFormatter() {
			  @Override
			  public String formatLabel(double value, boolean isValueX) {
			    if (!isValueX) {
			    	
			      if (value <= 1.0) {
			        return "WC";
			      } else if (value <= 2.0) {
			        return "Mod";
			      } else {
			        return "Sev";
			      }
			    }
			    return null; // let graphview generate x-axis label for us
			  }
			});

		graphViewPain.getGraphViewStyle().setNumHorizontalLabels(mCheckInArrayList.size());
		graphViewPain.getGraphViewStyle().setNumVerticalLabels(9);
		graphViewPain.setManualYAxisBounds(3, 0);

		graphViewPain.addSeries(seriesPain); // data
		
		//Eating Affect graph
		
		GraphViewSeries seriesEatingAffect = new GraphViewSeries("Eating Affect", seriesStyle, dataEatingAffect);
		GraphView graphViewEatingAffect = new BarGraphView(
		    this // context
		    , "EatingAffect" // heading
		);
		
		//labels for pain graph Y axis
		graphViewEatingAffect.setCustomLabelFormatter(new CustomLabelFormatter() {
			  @Override
			  public String formatLabel(double value, boolean isValueX) {
			    if (!isValueX) {
			    	
			      if (value <= 1.0) {
			        return "None";
			      } else if (value <= 2.0) {
			        return "Some";
			      } else {
			        return "CAN'T!";
			      }
			    }
			    return null; // let graphview generate x-axis label for us
			  }
			});

		graphViewEatingAffect.getGraphViewStyle().setNumHorizontalLabels(mCheckInArrayList.size());
		graphViewEatingAffect.getGraphViewStyle().setNumVerticalLabels(9);
		graphViewEatingAffect.setManualYAxisBounds(3, 0);

		graphViewEatingAffect.addSeries(seriesEatingAffect); // data
		
		LinearLayout layoutPain = (LinearLayout) findViewById(R.id.llGraphPain);
		layoutPain.addView(graphViewPain);
		LinearLayout layoutEatingAffect = (LinearLayout) findViewById(R.id.llGraphEatingAffect);
		layoutEatingAffect.addView(graphViewEatingAffect);
		
		Button btnGraphCheckIns = (Button)findViewById(R.id.btnGraphCheckIns);
		btnGraphCheckIns.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// go to Doctor's list of CheckIns for this Patient
				// TODO Auto-generated method stub
	    		Intent checkInIntent = new Intent(PatientGraphActivity.this, DoctorCheckInListActivity.class);
	    		checkInIntent.putExtra("serverPrefs", mServerPrefs);
	    		checkInIntent.putExtra("userName", mUserName);
	    		checkInIntent.putExtra("password", mPassword);
	    		checkInIntent.putExtra("user", mLoginUser);
	    		checkInIntent.putExtra("patient", mPatient);
	    		startActivity(checkInIntent);

			}
			
		});
		
		Button btnGraphMedications = (Button)findViewById(R.id.btnGraphMedications);
		btnGraphMedications.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// go to view or update the list of medications for this patient
	    		Intent medicationIntent = new Intent(PatientGraphActivity.this, PatientMedicationListActivity.class);
	    		medicationIntent.putExtra("serverPrefs", mServerPrefs);
	    		medicationIntent.putExtra("userName", mUserName);
	    		medicationIntent.putExtra("password", mPassword);
	    		medicationIntent.putExtra("user", mLoginUser);
	    		medicationIntent.putExtra("patient", mPatient);
	    		startActivity(medicationIntent);

			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_graph, menu);
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
			    	
			    	// Have to get the patient record from server again 
			    	//otherwise an "unsaved transient instance" 
			    	// error occurs
			    	// set patient reference in checkin to newly retrieved value
			    	mPatient = mSymptomCheckerService.getPatientById(mPatient.getPatientId());
			    	
			    	// get this patient's checkin data from the server for the last 48 hours
			    	Calendar cal = Calendar.getInstance();
			    	cal.add(Calendar.HOUR, -48);
			    	mCheckIns =  
			    			mSymptomCheckerService.findCheckInByCiPatientId(mPatient.getPatientId(),
			    					cal.getTimeInMillis());
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

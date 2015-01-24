package au.com.azarel.symptomchecker;

import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DoctorSettingsActivity extends ActionBarActivity {
	// allows doctor to set interval in minutes at which patient status is polled on server
	private int mIntIntervalMins;
	private long mLastAlarmId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_settings);
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		 mIntIntervalMins = preferences.getInt("doctorIntervalMins",15);
		 mLastAlarmId = preferences.getLong("lastAlarmId", 0);
		 EditText etIntervalMins = (EditText)findViewById(R.id.etIntervalMinutes);
		 etIntervalMins.setText(String.valueOf(mIntIntervalMins));
		 Button btnOk = (Button)findViewById(R.id.btnDoctorSettingsOK);
		 btnOk.setOnClickListener(new OnClickListener(){

			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			 	//save or resave server string
				SharedPreferences prefsOnClick = PreferenceManager.getDefaultSharedPreferences(DoctorSettingsActivity.this);
		        SharedPreferences.Editor editor = prefsOnClick.edit();
				EditText etIntervalMins = (EditText)findViewById(R.id.etIntervalMinutes);
		        editor.putInt("doctorIntervalMins", Integer.parseInt(etIntervalMins.getText().toString()));
		        editor.apply();
				
		        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		        
				if (mLastAlarmId > 0){
		        	// cancel previous alarm
					 Intent intent = new Intent(DoctorSettingsActivity.this, DoctorAlertService.class);
					 intent.setData(Uri.parse("custom://" + String.valueOf(mLastAlarmId)));
					 // Prepare the pending intent
					 PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					 // Register the alert in the system. You have the option to define if the device has to wake up on the alert or not
					 try {
						 alarmManager.cancel(pendingIntent);
					 } catch(Exception e){
						 Log.e("SymptomChecker", "Doctor Alarm was not cancelled, " + e.getMessage());
					 }
		        }
				
				int intervalMins = prefsOnClick.getInt("doctorIntervalMins", 0);
		        if (intervalMins > 0){
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					// give a one minute wait before the first alarm is triggered
					 calendar.add(Calendar.MINUTE, 1);
					// generate an id for this alarm
					long id = (long) System.currentTimeMillis();
					// Prepare the intent which should be launched at the date
					Intent intent = new Intent(DoctorSettingsActivity.this, DoctorAlertService.class);
					intent.setData(Uri.parse("custom://" + String.valueOf(id)));
					// Prepare the pending intent
					PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					// Register the alert in the system. You have the option to define if the device has to wake up on the alert or not
					alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60000 * intervalMins, pendingIntent);
				    //store alarm id for later cancellation if we reset the alarm interval   
					editor.putLong("lastAlarmId", id);
				    editor.apply();
		        }
		        //close this activity
		        finish();
			}
			 
		 });

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.doctor_settings, menu);
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
}

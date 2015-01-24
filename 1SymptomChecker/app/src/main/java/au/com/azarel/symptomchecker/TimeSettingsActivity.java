package au.com.azarel.symptomchecker;

import java.util.ArrayList;
import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
//import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeSettingsActivity extends ActionBarActivity {
	// allow patient to set their check in reminder times
	Calendar mDateAndTime;
	int mListViewIndex;
	TimeListAdapter timesAdapter;
	String mOriginalAlarmIds;
	String mNewTimeSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_settings);
		ListView listView;
		listView = (ListView) findViewById(R.id.time_list);
		  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		  mNewTimeSettings = preferences.getString("times", "09:00;12:00;17:00;20:00");
		 
		  mOriginalAlarmIds = preferences.getString("previousAlarms", "");
		String[] times = mNewTimeSettings.split(";");
		final ArrayList<String> timesArray = new ArrayList<String>();
		for (int i = 0; i < times.length; i++ ){
			timesArray.add(times[i]);
		}
		mDateAndTime=Calendar.getInstance();

		

		timesAdapter = new TimeListAdapter(this, timesArray);

		listView.setAdapter(timesAdapter);
		
		// save button
		Button btnSave = (Button)findViewById(R.id.btnOK);

		btnSave.setOnClickListener(new View.OnClickListener() {
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			public void onClick(View v) {
		        // Do something in response to button click
				ListView listView1 = (ListView) findViewById(R.id.time_list);
		        //View innerView;
		        ArrayList<String> timesToSave = new ArrayList<String>();
		        
		        // save new ly entered times in semi colon delimited string
		        for (int i = 0; i < listView1.getCount(); i++) {
		            //innerView = listView1.getAdapter().getView(i, null, null);
		        	View innerView = listView1.getChildAt(i);
		            TextView tv = (TextView) innerView.findViewById(R.id.tvTime);
		            timesToSave.add(tv.getText().toString());
		        }
		        StringBuilder sb = new StringBuilder();
		        for (int i = 0; i < timesToSave.size(); i++) {
		        	if (i>0) {
		        		sb.append(";");		        		
		        	}
		        	sb.append(timesToSave.get(i));
		        }
		        mNewTimeSettings = sb.toString();
		        
		        boolean bResult = SetResetNotificationTimes(mOriginalAlarmIds, mNewTimeSettings);
				SharedPreferences prefsOnClick = PreferenceManager.getDefaultSharedPreferences(TimeSettingsActivity.this);
		        SharedPreferences.Editor editor = prefsOnClick.edit();
		        editor.putString("times",sb.toString());
		        editor.apply();
		        Toast.makeText(TimeSettingsActivity.this, "Times saved and Check In reminders set", Toast.LENGTH_SHORT)
	            .show();

		        finish();
		    }
		});

		Button btnAdd = (Button)findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//add new time item, default to 12:00
				timesArray.add("12:00");
				timesAdapter.notifyDataSetChanged();
			}
		});
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_settings, menu);
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
	public void updateLabel(){
		
		ListView listView = (ListView) findViewById(R.id.time_list);

		View selectedView = listView.getChildAt(mListViewIndex);
		TextView tv = (TextView) selectedView.findViewById(R.id.tvTime); 
		tv.setText(mDateAndTime.toString());
	}
	
	private boolean SetResetNotificationTimes(String OriginalIds, String NewTimes) {
		Calendar calendar = Calendar.getInstance();
		  calendar.setTimeInMillis(System.currentTimeMillis());
		  calendar.add(Calendar.MINUTE, 1);
		  
		  //find the start of today
		  Calendar calStartToday = Calendar.getInstance();
		  calStartToday.clear();
		  calStartToday.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		  
		  /*
		   * Cancel all previous alarms then add all new alarms
		   */
		  
		  AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		  // original id's are alarm id's of previous alarms which we use to cancel them
		  String[] idsOriginal = OriginalIds.split(";");
		  // new times for alarms
		  String[] timesNew = NewTimes.split(";");
		  
		  for (int iO = 0; iO < idsOriginal.length; iO++) {
			  //cancel earlier alarms
			  	if (idsOriginal[iO] != ""){
					 // Prepare the intent which should be launched at the date
					 Intent intent = new Intent(this, CheckInReceiver.class);
					 intent.setData(Uri.parse("custom://" + String.valueOf(idsOriginal[iO])));
					 //intent.setAction(String.valueOf(id + iN));
					 // Prepare the pending intent
					 PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					 // Cancel the alarm related to this PendingIntent
					 try {
						 alarmManager.cancel(pendingIntent);
					 } catch(Exception e){
						 Log.e("SymptomChecker", "Checkin Alarm was not cancelled, " + e.getMessage());
					 }
			  	}		  
		  }

		  boolean bStringFirst = true;
		  String strNewIds = "";
		  // Every scheduled intent needs a different ID, else it is just executed once
		  long id = System.currentTimeMillis();
		  Calendar calNow = Calendar.getInstance();
		  Calendar calStartDay = Calendar.getInstance();
		  calStartDay.clear();
		  calStartDay.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		  
		  for (int iN = 0; iN < timesNew.length; iN++) {
			  //create new alarms
			  String[] HourMin = timesNew[iN].split(":");
			  if (HourMin.length < 2) {
				  //parse error on time
			  }
			  Calendar calAlarm = Calendar.getInstance();
			  calAlarm.clear();
			  calAlarm.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			  calAlarm.add(Calendar.HOUR, Integer.parseInt(HourMin[0]));
			  calAlarm.add(Calendar.MINUTE, Integer.parseInt(HourMin[1]));
			  // If resulting date time is in the past, make it the same time tomorrow
			  if (calAlarm.getTimeInMillis() <= calNow.getTimeInMillis()){
				  calAlarm.add(Calendar.SECOND, 86400);
			  }
				  
				 // Prepare the intent which should be launched at the date
				 Intent intent = new Intent(this, CheckInReceiver.class);
				 intent.setData(Uri.parse("custom://" + String.valueOf(id + iN)));
				 //intent.setAction(String.valueOf(id + iN));
				 // Prepare the pending intent
				 PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				 
				 // Register the alert in the system. You have the option to define if the device has to wake up on the alert or not
				 // setInexactRepeating was too inexact to be useful for demonstations in Genymotion
				 
				 //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60000, pendingIntent);
				 alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calAlarm.getTimeInMillis(),86400000, pendingIntent);
				 //store new alarmIds in preferences, to allow them to be cancelled in future if times are revised
				 strNewIds += (bStringFirst ? "" : ";") + String.valueOf(id + iN);
				 bStringFirst = false;
		  }
			SharedPreferences prefsOnClick = PreferenceManager.getDefaultSharedPreferences(TimeSettingsActivity.this);
	        SharedPreferences.Editor editor = prefsOnClick.edit();
	        editor.putString("previousAlarms",strNewIds);
	        editor.apply();


		return true;
	}
}

package au.com.azarel.symptomchecker;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import au.com.azarel.symptomcheckerservice.repository.Doctor;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DoctorAlertReceiver extends BroadcastReceiver {
	// receiver for broadcast from DoctorAlertService, when patients with reportable pain
	// or eating issues have been found
	
	private static final int MY_NOTIFICATION_ID = 1;
	private Doctor mDoctor;
	private String mCriticalPatients;
	
	public DoctorAlertReceiver() {
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mDoctor = intent.getParcelableExtra("doctor");
		// list of patients with reportable issues identified by DoctorAlertService
		mCriticalPatients = intent.getStringExtra("criticalPatients");
		
		// We go to the StartUp / login page for the Doctor to check in
		// If I had time I would pass extras in the intent to go to the DoctorPatientListActivity if the doctor was logged in
		Intent newIntent = new Intent(context, 
				StartActivity.class);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder notificationBuilder = new Notification.Builder(
				context)
				.setSmallIcon(android.R.drawable.stat_sys_warning)
				.setAutoCancel(true)
				.setContentTitle("Symptom Checker")
				.setTicker("You have patient(s) with critical issues, Dr " + mDoctor.getFirstName() + " " + mDoctor.getLastName())
				.setStyle(new Notification.BigTextStyle().bigText("You have patient(s) with critical issues:\n" + mCriticalPatients)) 
				.setContentIntent(pendingIntent);
		notificationManager.notify(MY_NOTIFICATION_ID,
				notificationBuilder.build());
		//throw new UnsupportedOperationException("Not yet implemented");
	}
}

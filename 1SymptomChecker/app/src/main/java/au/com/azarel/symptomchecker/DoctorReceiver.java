package au.com.azarel.symptomchecker;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class DoctorReceiver extends BroadcastReceiver {
	// NOT IN USE
	
	private static final int MY_NOTIFICATION_ID = 2;

	public DoctorReceiver() {
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// We go to the StartUp / login page for the Doctor to sign in
		Intent newIntent = new Intent(context, 
				StartActivity.class);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder notificationBuilder = new Notification.Builder(
				context)
				.setSmallIcon(android.R.drawable.stat_sys_warning)
				.setAutoCancel(true)
				.setContentTitle("Symptom Checker")
				.setTicker("One or more patient(s) need your attention!")
				.setContentText("Patients need your help").setContentIntent(pendingIntent);
		notificationManager.notify(MY_NOTIFICATION_ID,
				notificationBuilder.build());

		//throw new UnsupportedOperationException("Not yet implemented");
	}

}

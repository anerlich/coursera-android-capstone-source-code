package au.com.azarel.symptomchecker;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class CheckInReceiver extends BroadcastReceiver {
	//receiver for patient check in reminder alarm - to send reminder notification
	private static final int MY_NOTIFICATION_ID = 1;

	public CheckInReceiver() {
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// We go to the StartUp / login page for the Patient to check in
		// If I had time I would pass extras in the intent to go to the CheckInActivity if the patient was logged in
		Intent newIntent = new Intent(context, 
				StartActivity.class);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder notificationBuilder = new Notification.Builder(
				context)
				.setSmallIcon(android.R.drawable.stat_sys_warning)
				.setAutoCancel(true)
				.setContentTitle("Symptom Checker")
				.setTicker("Time for your check in!")
				.setContentText("Time for your check in!").setContentIntent(pendingIntent);
		notificationManager.notify(MY_NOTIFICATION_ID,
				notificationBuilder.build());

	}
}

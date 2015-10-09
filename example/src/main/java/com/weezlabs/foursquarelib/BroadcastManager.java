package com.weezlabs.foursquarelib;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.weezlabs.forsquarelib.LocationChecker;

public class BroadcastManager extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LOG", "onReceive");
		int signalId = intent.getIntExtra(LocationChecker.INTENT_EXTRA_TYPE, 0);
		Log.d("LOG", "signalId = " + signalId);
		Notification(context, LocationChecker.Signal.getTypeById(signalId).toString());

	}

	public void Notification(Context context, String message) {
		// Set Notification Title
		String strtitle = context.getString(R.string.notification_title);
		// Open NotificationView Class on Notification Click
		Intent intent = new Intent(context, MainActivity.class);
		// Send data to NotificationView Class
		intent.putExtra("title", strtitle);
		intent.putExtra("text", message);
		// Open NotificationView.java Activity
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Create Notification using NotificationCompat.Builder
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				// Set Icon
				.setSmallIcon(context.getApplicationInfo().icon)
						// Set Ticker Message
				.setTicker(message)
						// Set Title
				.setContentTitle(context.getString(R.string.notification_title))
						// Set Text
				.setContentText(message)
						// Add an Action Button below Notification
				.addAction(context.getApplicationInfo().icon, "Action Button", pIntent)
						// Set PendingIntent into Notification
				.setContentIntent(pIntent)
						// Dismiss Notification
				.setAutoCancel(true);

		// Create Notification Manager
		NotificationManager notificationmanager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Build Notification with Notification Manager
		notificationmanager.notify(0, builder.build());

	}
}

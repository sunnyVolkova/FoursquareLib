package com.weezlabs.forsquarelib;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Service for checking user location and state, uses LocationChecker
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
	public static final String LOG_TAG = "LocationService";
	public static final String CHECKER_EXTRA = "CHECKER_EXTRA";
	private static final int DEFAULT_NOTIFICATION_ID = 1;

	protected LocationChecker checker_;
	protected LocationRequest locationRequest_;
	protected Location lastLocation_;
	protected GoogleApiClient googleApiClient_;
	private static boolean isStarted_ = false;

	public static boolean isStarted(){
		return isStarted_;
	}
	@Override
	public void onConnected(Bundle bundle) {
		if (lastLocation_ == null) {
			lastLocation_ = LocationServices.FusedLocationApi.getLastLocation(googleApiClient_);

		}
		checker_.checkLocation(getBaseContext(), lastLocation_);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				googleApiClient_, locationRequest_, this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(LOG_TAG, "onConnectionSuspended");
		googleApiClient_.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(LOG_TAG, "onConnectionFailed");
	}

	@Override
	public void onLocationChanged(Location location) {
		lastLocation_ = location;
		checker_.checkLocation(getBaseContext(), lastLocation_);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		CheckParams checkParams = intent.getParcelableExtra(CHECKER_EXTRA);
		if(checkParams != null){
			startMyLocationCheck(checkParams);
		} else {
			startMyLocationCheck();
		}
		return(START_NOT_STICKY);
	}

	/**
	 * Starts location check with default parameters
	 */
	public void startMyLocationCheck() {
		startMyLocationCheck(new CheckParams());
	}

	/**
	 * Starts location check with custom checker
	 */
	public void startMyLocationCheck(@Nullable CheckParams checkParams) {
		if(!isStarted_) {
			Log.d(LOG_TAG, "LocationService startMyLocationCheck");
			checker_ = new LocationChecker(checkParams);
			buildGoogleApiClient();
			createLocationRequest(checker_.getCheckParams().getLocationUpdatePeriodMs());
			googleApiClient_.connect();
			startForeground(createDefaultNotification());
			isStarted_ = true;
		}
	}

	public void stopMyLocationCheck() {
		Log.d(LOG_TAG, "LocationService stopMyLocationCheck");
		isStarted_ = false;
		if (googleApiClient_.isConnected()) {
			googleApiClient_.disconnect();
		}
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "LocationService onDestroy");
		stopMyLocationCheck();
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected synchronized void buildGoogleApiClient() {
		googleApiClient_ = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
	}

	protected void createLocationRequest(long updateInterval) {
		locationRequest_ = new LocationRequest();
		locationRequest_.setInterval(updateInterval);
		locationRequest_.setFastestInterval(updateInterval / 2);
		locationRequest_.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	}


	private void startForeground(final Notification notification) {
		try {
			final Method setForegroundMethod = Service.class.getMethod("startForeground", int.class, Notification.class);
			setForegroundMethod.invoke(this, DEFAULT_NOTIFICATION_ID, notification);
		} catch (final SecurityException e) {
			Log.e(LOG_TAG, "Unable to start a service in foreground", e);
		} catch (final NoSuchMethodException e) {
			Log.e(LOG_TAG, "Unable to start a service in foreground", e);
		} catch (final IllegalArgumentException e) {
			Log.e(LOG_TAG, "Unable to start a service in foreground", e);
		} catch (final IllegalAccessException e) {
			Log.e(LOG_TAG, "Unable to start a service in foreground", e);
		} catch (final InvocationTargetException e) {
			Log.e(LOG_TAG, "Unable to start a service in foreground", e);
		}
	}

	/**
	 * Could be overrided to create appropriate notification
	 * */
	protected Notification createDefaultNotification() {
		Notification notification = null;
		String notificationTitle = getString(R.string.notification_title);
		String notificationText = getString(R.string.notification_text);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			notification = new Notification.Builder(this).setSmallIcon(getApplicationInfo().icon).setContentText(notificationText).setContentTitle(notificationTitle).build();
		} else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			notification = new Notification.Builder(this).setSmallIcon(getApplicationInfo().icon).setContentText(notificationText).setContentTitle(notificationTitle).getNotification();
		} else {
			notification = new NotificationCompat.Builder(this).setSmallIcon(getApplicationInfo().icon).build();
		}

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			notification.priority = Notification.PRIORITY_MIN;
		}
		notification.flags|=Notification.FLAG_NO_CLEAR;
		return notification;
	}
}

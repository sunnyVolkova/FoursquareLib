package com.weezlabs.forsquarelib;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
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
	private static final int DEFAULT_NOTIFICATION_ID = 1;

	private LocationChecker checker_;
	private final IBinder binder_ = new LocalBinder();

	private GoogleApiClient googleApiClient_;
	protected LocationRequest locationRequest_;
	protected Location lastLocation_;
	private boolean isStarted_ = false;

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

	/**
	 * Class used for the client Binder. Because we know this service always runs in the same process as its clients, we don't
	 * need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public LocationService getService() {
			// Return this instance of LocalService so clients can call public methods
			return LocationService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return(START_NOT_STICKY);
	}


	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "LocationService onBind");
		return binder_;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(LOG_TAG, "LocationService onRebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(LOG_TAG, "LocationService onUnbind");
		return super.onUnbind(intent);
	}

	/**
	 * Starts location check with default parameters
	 */
	public void startMyLocationCheck() {
		startMyLocationCheck(new LocationChecker());
	}

	/**
	 * Starts location check with custom checker
	 */
	public void startMyLocationCheck(LocationChecker checker) {
		if(!isStarted_) {
			Log.d(LOG_TAG, "LocationService startMyLocationCheck");
			checker_ = checker;
			buildGoogleApiClient();
			createLocationRequest(checker_.getLocationUpdatePeriodMs());
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

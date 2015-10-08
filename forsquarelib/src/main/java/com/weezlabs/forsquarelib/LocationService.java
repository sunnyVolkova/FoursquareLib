package com.weezlabs.forsquarelib;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Service for checking user location and state, uses LocationChecker
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
	LocationChecker checker_;

	private Thread myLocationCheckThread_;
	private final IBinder binder_ = new LocalBinder();

	//variables for google API
	/**
	 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
	 */
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

	/**
	 * The fastest rate for active location updates. Exact. Updates will never be more frequent
	 * than this value.
	 */
	public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
			UPDATE_INTERVAL_IN_MILLISECONDS / 2;

	private GoogleApiClient googleApiClient_;
	protected LocationRequest locationRequest_;
	protected Location lastLocation_;

	@Override
	public void onConnected(Bundle bundle) {
		if (lastLocation_ == null) {
			lastLocation_ = LocationServices.FusedLocationApi.getLastLocation(googleApiClient_);

		}
		LocationServices.FusedLocationApi.requestLocationUpdates(
				googleApiClient_, locationRequest_, this);
//		lastLocation_ = LocationServices.FusedLocationApi.getLastLocation(googleApiClient_);
//		if (lastLocation_ != null) {
//			Log.d("LOG", "getLastLocation success");
//			//TODO: success
//		} else {
//			Log.d("LOG", "getLastLocation error");
//			//TODO: error
//		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("LOG", "onConnectionSuspended");
		googleApiClient_.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("LOG", "onConnectionFailed");
	}

	@Override
	public void onLocationChanged(Location location) {
		lastLocation_ = location;
		Log.d("LOG", "onLocationChanged " + lastLocation_.getLongitude() + " " + lastLocation_.getLatitude());
		//TODO:
		checker_.checkLocation2(getBaseContext(), lastLocation_);
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
	public void onCreate() {
		super.onCreate();

	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder_;
	}

	public void startMyLocationCheck(LocationChecker checker) {
		checker_ = checker;
		buildGoogleApiClient();
		createLocationRequest(checker_.getWaitForOutMs());
		googleApiClient_.connect();
	}

	public void stopMyLocationCheck() {
		if (googleApiClient_.isConnected()) {
			googleApiClient_.disconnect();
		}
	}

	@Override
	public void onDestroy() {
		stopMyLocationCheck();
		super.onDestroy();
	}

	public void startMyLocationCheck() {
		startMyLocationCheck(new LocationChecker());
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
		locationRequest_.setFastestInterval(updateInterval/2);
		locationRequest_.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	}
}

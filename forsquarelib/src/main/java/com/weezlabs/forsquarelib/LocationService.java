package com.weezlabs.forsquarelib;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
/**
 * Service for checking user location and state, uses LocationChecker
 */
public class LocationService extends Service {
	LocationChecker checker_;

	private Thread myLocationCheckThread_;
	private final IBinder binder_ = new LocalBinder();

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
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder_;
	}

	public void startMyLocationCheck() {
		startMyLocationCheck(new LocationChecker());
	}

	public void startMyLocationCheck(LocationChecker checker) {
		checker_ = checker;
		if (myLocationCheckThread_ != null && myLocationCheckThread_.isAlive()) {
			myLocationCheckThread_.interrupt();
		}

		myLocationCheckThread_ = new Thread(new Runnable() {
			private int waitTime_ = checker_.getWaitForOutMs();
			@Override
			public void run() {
				Log.d("LOG", "run");
				while (!myLocationCheckThread_.isInterrupted()) {
					try {
						waitTime_ = checker_.checkLocation(getBaseContext());
						Thread.sleep(waitTime_);
					} catch (InterruptedException e) {
						break;
					}
				}
			}

		});
		myLocationCheckThread_.start();
	}

	public void stopMyLocationCheck() {
		Log.d("LOG", "stopMyLocationCheck");
		if (myLocationCheckThread_ != null) {
			myLocationCheckThread_.interrupt();
		}
	}
}

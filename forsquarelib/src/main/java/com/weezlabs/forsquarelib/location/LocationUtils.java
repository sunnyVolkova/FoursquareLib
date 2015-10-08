package com.weezlabs.forsquarelib.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
/**
 * Class for implementing operations with location
 */
public class LocationUtils {
	public static Location getMyLocationWithPermission(Context context) {

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// only for gingerbread and newer versions
			if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return getMyLocation(context);
			}
		} else {
			return getMyLocation(context);
		}

		return new Location("");
	}

	private static Location getMyLocation(Context context){
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (myLocation == null) {
			myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return myLocation;
	}

	public static double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
		float[] results = new float[1];
		Location.distanceBetween(lat1, lng1, lat2, lng2, results);
		return results[0];
	}
}

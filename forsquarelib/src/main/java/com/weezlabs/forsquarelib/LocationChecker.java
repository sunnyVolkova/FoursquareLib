package com.weezlabs.forsquarelib;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.weezlabs.forsquarelib.foursquare.ForsquareProvider;
import com.weezlabs.forsquarelib.location.LocationUtils;
import com.weezlabs.forsquarelib.models.SearchVenuesResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
//TODO: create builder ???

/**
 * Class for implementing customizable location check logic
 */
public class LocationChecker {
	public enum UserState {
		IN, STAY, EATING, FINISHED_EATING, OUT;
	}

	public class UserStateInfo {
		private UserState state;
		private long entered;
		public UserStateInfo(UserState state, long entered){
			this.state = state;
			this.entered = entered;
		}

		public void setState(UserState state){
			this.state = state;
			entered = System.currentTimeMillis();
		}

		public UserState getState(){
			return state;
		}

		public long getEntered(){
			return entered;
		}
	}

	public enum Signal {
		BEFORE_EAT(0), EATING(1), FINISH_EATING(2);
		private int id_;

		private Signal(int id) {
			id_ = id;
		}

		public int getId() {
			return id_;
		}

		public static Signal getTypeById(int id) {
			for (Signal type : Signal.values()) {
				if (type.getId() == id) {
					return type;
				}
			}

			return null;
		}
	}

	//broadcast parameters
	public static final String INTENT_FILTER_DR_SIGNAL = "INTENT_FILTER_DR_SIGNAL";
	public static final String INTENT_EXTRA_TYPE = "TYPE";

	//Forsquare categories
	public static final String FOOD_CATEGORY_ID = "4d4b7105d754a06374d81259";

	//default check parameters
	private static final int WAIT_FOR_STAY_MS = 90000;
	private static final int WAIT_FOR_ORDER_MS = 150000;
	private static final int WAIT_FOR_STOP_EATING = 300000;
	private static final int LOCATION_UPDATE_PERIOD_MS = 300000;
	private static final int DEFAULT_LOCATION_CHECK_THRESHOLD = 100;
	private static final String SEARCH_RADIUS = "100";
	private static final String SEARCH_LIMIT = "1";
	private static final String[] DEFAULT_CATEGORIES = {FOOD_CATEGORY_ID};

	//check parameters
	private ArrayList<String> categoryList_ = new ArrayList<>();
	private int waitForStayMs_ = WAIT_FOR_STAY_MS;
	private int waitForOrderMs_ = WAIT_FOR_ORDER_MS;
	private int waitForStopEatingMs_ = WAIT_FOR_STOP_EATING;
	private int locationUpdatePeriodMs = LOCATION_UPDATE_PERIOD_MS;
	private int locationCheckThreshold_ = DEFAULT_LOCATION_CHECK_THRESHOLD;
	private String searchRadius_ = SEARCH_RADIUS;
	private String searchLimit_ = SEARCH_LIMIT;

	//variables for check process
	private UserStateInfo userStateInfo_ = new UserStateInfo(UserState.OUT, System.currentTimeMillis());
	private double lastKnownLatitude_ = 0.0;
	private double lastKnownLongitude_ = 0.0;
	private int waitTime_ = locationUpdatePeriodMs;
	private String currentVenueId_ = "";

	public LocationChecker() {
		categoryList_.addAll(Arrays.asList(DEFAULT_CATEGORIES));
		lastKnownLatitude_ = 0.0;
		lastKnownLongitude_ = 0.0;
	}

	public void addCategory(String categoryId) {
		categoryList_.add(categoryId);
	}

	public void setWaitForStayMs(int waitForStayMs) {
		this.waitForStayMs_ = waitForStayMs;
	}

	public void setWaitForOrderMs(int waitForOrderMs) {
		this.waitForOrderMs_ = waitForOrderMs;
	}

	public void setWaitForStopEatingMs(int waitForStopEatingMs) {
		this.waitForStopEatingMs_ = waitForStopEatingMs;
	}

	public void setLocationUpdatePeriodMs(int waitForOutMs) {
		this.locationUpdatePeriodMs = waitForOutMs;
	}

	public int getLocationUpdatePeriodMs() {
		return locationUpdatePeriodMs;
	}

	public void setSearchRadius(String searchRadius) {
		this.searchRadius_ = searchRadius;
	}

	public void setSearchLimit(String searchLimit) {
		this.searchLimit_ = searchLimit;
	}

	public void setLocationCheckThreshold(int locationCheckThreshold) {
		this.locationCheckThreshold_ = locationCheckThreshold;
	}



	/**
	 * Main method for checking user state according to location and prev state
	 *
	 * @return time delay before next check in ms
	 */
	public int checkLocation(final Context context, Location myLocation) {
		if (myLocation != null) {
			double latitude = myLocation.getLatitude();
			double longitude = myLocation.getLongitude();
			Log.d("LOG", "latitude = " + latitude + " longitude = " + longitude);
			Log.d("LOG", "userStateInfo_ = " + userStateInfo_.getState() + " userStateInfo_.getEntered() = " + userStateInfo_.getEntered());
			//TODO: probably should check distance in UserState.FINISHED_EATING state???
			if (checkIfTimeoutExpired() &&
					(userStateInfo_.getState() != UserState.OUT
					||
					Math.abs(LocationUtils.distanceFrom(lastKnownLatitude_, lastKnownLongitude_, latitude, longitude)) > locationCheckThreshold_)) {
				String ll = String.format(Locale.US, "%.06f", latitude) + "," + String.format(Locale.US, "%.06f", longitude);
				ForsquareProvider.getForsquareService().searchVenues(ll, searchRadius_, searchLimit_, getCategoriesString(), new Callback<SearchVenuesResponse>() {
					@Override
					public void success(SearchVenuesResponse searchVenuesResponse, Response response) {
						if (searchVenuesResponse.getVenues() != null && searchVenuesResponse.getVenues().length > 0 && searchVenuesResponse.getVenues()[0] != null) {
							Log.d("LOG", "success: " + searchVenuesResponse.getVenues()[0].getId());
							Log.d("LOG", "we are in: " + searchVenuesResponse.getVenues()[0].getName());
							changeStateIfIn(context, searchVenuesResponse.getVenues()[0].getId());
						} else {
							Log.d("LOG", "success: empty venues");
							changeStateIfOut(context);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.d("LOG", "failure " + error.getMessage());
						changeStateIfOut(context);
					}
				});
				lastKnownLatitude_ = latitude;
				lastKnownLongitude_ = longitude;
			}
		}
		return waitTime_;
	}

	private String getCategoriesString() {
		StringBuilder builder = new StringBuilder();
		for (String s : categoryList_) {
			builder.append(s);
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		Log.d("LOG", "Categories: " + builder.toString());
		return builder.toString();
	}

	private boolean checkIfTimeoutExpired(){
		long currentTime = System.currentTimeMillis();
		Log.d("LOG", "userStateInfo_.getEntered() = " + userStateInfo_.getEntered() + " currentTime = " + currentTime);
		switch (userStateInfo_.getState()) {
			case OUT:
				return true;
			case IN:
				return (currentTime - userStateInfo_.getEntered()) > waitForStayMs_;
			case STAY:
				return (currentTime - userStateInfo_.getEntered()) > waitForOrderMs_;
			case EATING:
				return (currentTime - userStateInfo_.getEntered()) > waitForStopEatingMs_;
			case FINISHED_EATING:
				return true;
		}
		return true;
	}

	private void changeStateIfOut(Context context) {
		currentVenueId_ = "";
		switch (userStateInfo_.getState()) {
			case OUT:
				waitTime_ = locationUpdatePeriodMs;
				break;
			case IN:
				userStateInfo_.setState(UserState.OUT);
				waitTime_ = locationUpdatePeriodMs;
				break;
			case STAY:
				userStateInfo_.setState(UserState.OUT);
				waitTime_ = locationUpdatePeriodMs;
				break;
			case EATING:
				sendSignal(context, Signal.FINISH_EATING);
				userStateInfo_.setState(UserState.OUT);
				waitTime_ = locationUpdatePeriodMs;
				break;
			case FINISHED_EATING:
				userStateInfo_.setState(UserState.OUT);
				waitTime_ = locationUpdatePeriodMs;
				break;
		}
	}

	private void changeStateIfIn(Context context, String venueId) {
		switch (userStateInfo_.getState()) {
			case OUT:
				userStateInfo_.setState(UserState.IN);
				waitTime_ = waitForStayMs_;
				currentVenueId_ = venueId;
				break;
			case IN:
				if (currentVenueId_.equals(venueId)) {
					userStateInfo_.setState(UserState.STAY);
					waitTime_ = waitForOrderMs_;
					sendSignal(context, Signal.BEFORE_EAT);
				} else {
					userStateInfo_.setState(UserState.IN);
					waitTime_ = waitForStayMs_;
					currentVenueId_ = venueId;
				}
				break;
			case STAY:
				if (currentVenueId_.equals(venueId)) {
					userStateInfo_.setState(UserState.EATING);
					waitTime_ = waitForStopEatingMs_;
					sendSignal(context, Signal.EATING);
				} else {
					userStateInfo_.setState(UserState.IN);
					waitTime_ = waitForStayMs_;
					currentVenueId_ = venueId;
				}
				break;
			case EATING:
				if (currentVenueId_.equals(venueId)) {
					userStateInfo_.setState(UserState.FINISHED_EATING);
					waitTime_ = locationUpdatePeriodMs;
					sendSignal(context, Signal.FINISH_EATING);
				} else {
					userStateInfo_.setState(UserState.IN);
					waitTime_ = waitForStayMs_;
					currentVenueId_ = venueId;
					sendSignal(context, Signal.FINISH_EATING);
				}
				break;
			case FINISHED_EATING:
				if (currentVenueId_.equals(venueId)) {
					userStateInfo_.setState(UserState.FINISHED_EATING);
					waitTime_ = locationUpdatePeriodMs;
				} else {
					userStateInfo_.setState(UserState.IN);
					waitTime_ = waitForStayMs_;
					currentVenueId_ = venueId;
				}
				break;
		}
	}

	/**
	 * Sends signal about user state change
	 */
	private void sendSignal(Context context, Signal signal) {
		Log.d("LOG", "sendSignal " + signal);
		Intent intent = new Intent(INTENT_FILTER_DR_SIGNAL);
		intent.putExtra(INTENT_EXTRA_TYPE, signal.getId());
		context.sendBroadcast(intent);
	}
}

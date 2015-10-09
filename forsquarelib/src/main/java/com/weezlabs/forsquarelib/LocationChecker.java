package com.weezlabs.forsquarelib;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.weezlabs.forsquarelib.foursquare.ForsquareProvider;
import com.weezlabs.forsquarelib.location.LocationUtils;
import com.weezlabs.forsquarelib.models.SearchVenuesResponse;

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
		IN, STAY, EATING, FINISHED_EATING, OUT
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

	public static final String LOG_TAG = "LocationChecker";
	//broadcast parameters
	public static final String INTENT_FILTER_DR_SIGNAL = "INTENT_FILTER_DR_SIGNAL";
	public static final String INTENT_EXTRA_TYPE = "TYPE";

	//Forsquare categories
	public static final String FOOD_CATEGORY_ID = "4d4b7105d754a06374d81259";

	//check parameters
	private CheckParams checkParams_;

	//variables for check process
	private UserStateInfo userStateInfo_ = new UserStateInfo(UserState.OUT, System.currentTimeMillis());
	private double lastKnownLatitude_ = 0.0;
	private double lastKnownLongitude_ = 0.0;
	private long waitTime_ = CheckParams.LOCATION_UPDATE_PERIOD_MS;
	private String currentVenueId_ = "";
	private boolean prevRequestFailed_ = false;

	public LocationChecker(CheckParams checkParams) {
		checkParams_ = checkParams;
		waitTime_ = checkParams_.locationUpdatePeriodMs;
		lastKnownLatitude_ = 0.0;
		lastKnownLongitude_ = 0.0;
	}

	public void setCheckParams(CheckParams checkParams){
		checkParams_ = checkParams;
	}

	public CheckParams getCheckParams(){
		return checkParams_;
	}

	/**
	 * Main method for checking user state according to location and prev state
	 *
	 * @return time delay before next check in ms
	 */
	public long checkLocation(final Context context, Location myLocation) {
		if (myLocation != null) {
			double latitude = myLocation.getLatitude();
			double longitude = myLocation.getLongitude();

			//TODO: probably should check distance in UserState.FINISHED_EATING state???
			if (checkIfTimeoutExpired() &&
					(userStateInfo_.getState() != UserState.OUT
					|| prevRequestFailed_
					|| Math.abs(LocationUtils.distanceFrom(lastKnownLatitude_, lastKnownLongitude_, latitude, longitude)) > checkParams_.locationCheckThreshold_)) {
				String ll = String.format(Locale.US, "%.06f", latitude) + "," + String.format(Locale.US, "%.06f", longitude);
				ForsquareProvider.getForsquareService().searchVenues(ll, checkParams_.searchRadius_, checkParams_.searchLimit_, getCategoriesString(), new Callback<SearchVenuesResponse>() {
					@Override
					public void success(SearchVenuesResponse searchVenuesResponse, Response response) {
						prevRequestFailed_ = false;
						if (searchVenuesResponse.getVenues() != null && searchVenuesResponse.getVenues().length > 0 && searchVenuesResponse.getVenues()[0] != null) {
							Log.d(LOG_TAG, "success: " + searchVenuesResponse.getVenues()[0].getId());
							Log.d(LOG_TAG, "we are in: " + searchVenuesResponse.getVenues()[0].getName());
							changeStateIfIn(context, searchVenuesResponse.getVenues()[0].getId());
						} else {
							Log.d(LOG_TAG, "success: empty venues");
							changeStateIfOut(context);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.d(LOG_TAG, "failure " + error.getMessage());
						changeStateIfOut(context);
						prevRequestFailed_ = true;
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
		for (String s : checkParams_.categoryList_) {
			builder.append(s);
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		Log.d(LOG_TAG, "Categories: " + builder.toString());
		return builder.toString();
	}

	private boolean checkIfTimeoutExpired(){
		long currentTime = System.currentTimeMillis();
		Log.d(LOG_TAG, "userStateInfo_.getEntered() = " + userStateInfo_.getEntered() + " currentTime = " + currentTime);
		switch (userStateInfo_.getState()) {
			case OUT:
				return true;
			case IN:
				return (currentTime - userStateInfo_.getEntered()) > checkParams_.waitForStayMs_;
			case STAY:
				return (currentTime - userStateInfo_.getEntered()) > checkParams_.waitForOrderMs_;
			case EATING:
				return (currentTime - userStateInfo_.getEntered()) > checkParams_.waitForStopEatingMs_;
			case FINISHED_EATING:
				return true;
		}
		return true;
	}

	private void changeStateIfOut(Context context) {
		currentVenueId_ = "";
		switch (userStateInfo_.getState()) {
			case OUT:
				waitTime_ = checkParams_.locationUpdatePeriodMs;
				break;
			case IN:
				userStateInfo_.setState(UserState.OUT);
				waitTime_ = checkParams_.locationUpdatePeriodMs;
				break;
			case STAY:
				userStateInfo_.setState(UserState.OUT);
				waitTime_ = checkParams_.locationUpdatePeriodMs;
				break;
			case EATING:
				sendSignal(context, Signal.FINISH_EATING);
				userStateInfo_.setState(UserState.OUT);
				waitTime_ = checkParams_.locationUpdatePeriodMs;
				break;
			case FINISHED_EATING:
				userStateInfo_.setState(UserState.OUT);
				waitTime_ = checkParams_.locationUpdatePeriodMs;
				break;
		}
	}

	private void changeStateIfIn(Context context, String venueId) {
		switch (userStateInfo_.getState()) {
			case OUT:
				userStateInfo_.setState(UserState.IN);
				waitTime_ = checkParams_.waitForStayMs_;
				currentVenueId_ = venueId;
				break;
			case IN:
				if (currentVenueId_.equals(venueId)) {
					userStateInfo_.setState(UserState.STAY);
					waitTime_ = checkParams_.waitForOrderMs_;
					sendSignal(context, Signal.BEFORE_EAT);
				} else {
					userStateInfo_.setState(UserState.IN);
					waitTime_ = checkParams_.waitForStayMs_;
					currentVenueId_ = venueId;
				}
				break;
			case STAY:
				if (currentVenueId_.equals(venueId)) {
					userStateInfo_.setState(UserState.EATING);
					waitTime_ = checkParams_.waitForStopEatingMs_;
					sendSignal(context, Signal.EATING);
				} else {
					userStateInfo_.setState(UserState.IN);
					waitTime_ = checkParams_.waitForStayMs_;
					currentVenueId_ = venueId;
				}
				break;
			case EATING:
				if (currentVenueId_.equals(venueId)) {
					userStateInfo_.setState(UserState.FINISHED_EATING);
					waitTime_ = checkParams_.locationUpdatePeriodMs;
					sendSignal(context, Signal.FINISH_EATING);
				} else {
					userStateInfo_.setState(UserState.IN);
					waitTime_ = checkParams_.waitForStayMs_;
					currentVenueId_ = venueId;
					sendSignal(context, Signal.FINISH_EATING);
				}
				break;
			case FINISHED_EATING:
				if (currentVenueId_.equals(venueId)) {
					userStateInfo_.setState(UserState.FINISHED_EATING);
					waitTime_ = checkParams_.locationUpdatePeriodMs;
				} else {
					userStateInfo_.setState(UserState.IN);
					waitTime_ = checkParams_.waitForStayMs_;
					currentVenueId_ = venueId;
				}
				break;
		}
	}

	/**
	 * Sends signal about user state change
	 */
	private void sendSignal(Context context, Signal signal) {
		Log.d(LOG_TAG, "sendSignal " + signal);
		Intent intent = new Intent(INTENT_FILTER_DR_SIGNAL);
		intent.putExtra(INTENT_EXTRA_TYPE, signal.getId());
		context.sendBroadcast(intent);
	}
}

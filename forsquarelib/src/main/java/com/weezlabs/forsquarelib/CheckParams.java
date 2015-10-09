package com.weezlabs.forsquarelib;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Class to keep parameters of LocationChecker
 */
public class CheckParams implements Parcelable {
	//default check parameters
	static final long WAIT_FOR_STAY_MS = 90000;
	static final long WAIT_FOR_ORDER_MS = 150000;
	static final long WAIT_FOR_STOP_EATING = 300000;
	static final long LOCATION_UPDATE_PERIOD_MS = 300000;
	static final int DEFAULT_LOCATION_CHECK_THRESHOLD = 100;
	static final String SEARCH_RADIUS = "100";
	static final String SEARCH_LIMIT = "1";
	static final String[] DEFAULT_CATEGORIES = {LocationChecker.FOOD_CATEGORY_ID};

	ArrayList<String> categoryList_ = new ArrayList<>();
	long waitForStayMs_ = WAIT_FOR_STAY_MS;
	long waitForOrderMs_ = WAIT_FOR_ORDER_MS;
	long waitForStopEatingMs_ = WAIT_FOR_STOP_EATING;
	long locationUpdatePeriodMs = LOCATION_UPDATE_PERIOD_MS;
	int locationCheckThreshold_ = DEFAULT_LOCATION_CHECK_THRESHOLD;
	String searchRadius_ = SEARCH_RADIUS;
	String searchLimit_ = SEARCH_LIMIT;

	public CheckParams() {
		categoryList_.addAll(Arrays.asList(DEFAULT_CATEGORIES));
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

	public long getLocationUpdatePeriodMs() {
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(waitForStayMs_);
		dest.writeLong(waitForOrderMs_);
		dest.writeLong(waitForStopEatingMs_);
		dest.writeLong(locationUpdatePeriodMs);
		dest.writeInt(locationCheckThreshold_);
		dest.writeString(searchRadius_);
		dest.writeString(searchLimit_);
		dest.writeStringList(categoryList_);
	}

	public CheckParams(Parcel source) {
		waitForStayMs_ = source.readLong();
		waitForOrderMs_ = source.readLong();
		waitForStopEatingMs_ = source.readLong();
		locationUpdatePeriodMs = source.readLong();
		locationCheckThreshold_ = source.readInt();
		searchRadius_ = source.readString();
		searchLimit_ = source.readString();
		source.readStringList(categoryList_);
	}

	public static final Creator<CheckParams> CREATOR = new Creator<CheckParams>() {

		@Override
		public CheckParams[] newArray(int size) {
			return new CheckParams[size];
		}

		@Override
		public CheckParams createFromParcel(Parcel source) {
			return new CheckParams(source);
		}
	};
}

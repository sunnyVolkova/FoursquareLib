package com.weezlabs.forsquarelib.foursquare;

import retrofit.RestAdapter;

public class ForsquareProvider {
	public static final String HOST = "https://api.foursquare.com";
	public static final String API_ROOT = HOST + "/v2";
	public static final String URL_EXPLORE_VENUES = "/venues/explore";
	public static final String URL_SEARCH_VENUES = "/venues/search";
	public static final String FORSQUARE_VERSION = "20151006";

	public static final String URL_SEARCH_VENUES_UERLESS_ACCESS = URL_SEARCH_VENUES + "?v=" + FORSQUARE_VERSION;
	private static ForsquareService forsquareService_;

	public static ForsquareService getForsquareService() {
		if (forsquareService_ == null) {
			RestAdapter restAdapter = new RestAdapter.Builder()
					.setLogLevel(RestAdapter.LogLevel.FULL)
					.setEndpoint(API_ROOT)
					.build();
			forsquareService_ = restAdapter.create(ForsquareService.class);
		}
		return forsquareService_;
	}

}

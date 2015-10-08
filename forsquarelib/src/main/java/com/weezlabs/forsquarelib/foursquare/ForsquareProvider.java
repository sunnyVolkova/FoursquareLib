package com.weezlabs.forsquarelib.foursquare;

import retrofit.RestAdapter;

public class ForsquareProvider {
	public static final String HOST = "https://api.foursquare.com";
	public static final String API_ROOT = HOST + "/v2";
	public static final String URL_EXPLORE_VENUES = "/venues/explore";
	public static final String URL_SEARCH_VENUES = "/venues/search";
	public static final String FORSQUARE_CLIENT_ID = "YTZUJEICNYYBYWL4PRZWV13AXPVBUFLDOLTDXZP333HGVHJY";
	public static final String FORSQUARE_CLIENT_SECRET = "JHJY0FPUZCZQMADBNV2TQ4M0MIVDQJW0QXJVXCXVR4QP4SD5";
	public static final String FORSQUARE_VERSION = "20151006";

	public static final String URL_SEARCH_VENUES_UERLESS_ACCESS = URL_SEARCH_VENUES + "?client_id=" + FORSQUARE_CLIENT_ID + "&client_secret=" + FORSQUARE_CLIENT_SECRET + "&v=" + FORSQUARE_VERSION;
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

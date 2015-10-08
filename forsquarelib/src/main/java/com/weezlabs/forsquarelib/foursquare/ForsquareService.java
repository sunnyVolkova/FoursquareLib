package com.weezlabs.forsquarelib.foursquare;

import com.weezlabs.forsquarelib.models.SearchVenuesResponse;

import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ForsquareService {
	/**
	 * async method to explore venues
	 */
	@GET(ForsquareProvider.URL_EXPLORE_VENUES)
	void exploreVenues(@Query("client_id") String client_id,
					   @Query("client_secret") String client_secret,
					   @Query("ll") String ll,
					   @Query("radius") String radius,
					   @Query("limit") String limit,
					   @Query("sortByDistance") String sortByDistance,
					   @Query("v") String version,
					   ResponseCallback callback);

	/**
	 * async method to search venues
	 */
	@GET(ForsquareProvider.URL_SEARCH_VENUES_UERLESS_ACCESS)
	void searchVenues(
			@Query("ll") String ll,
			@Query("radius") String radius,
			@Query("limit") String limit,
			@Query("categoryId") String categoryId,
			Callback<SearchVenuesResponse> callback);

	/**
	 * sync method to search venues
	 */
	@GET(ForsquareProvider.URL_SEARCH_VENUES_UERLESS_ACCESS)
	SearchVenuesResponse searchVenues(
			@Query("ll") String ll,
			@Query("radius") String radius,
			@Query("limit") String limit,
			@Query("categoryId") String categoryId);
}

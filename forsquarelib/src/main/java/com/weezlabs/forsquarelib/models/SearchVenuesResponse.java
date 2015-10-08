package com.weezlabs.forsquarelib.models;

import com.google.gson.annotations.SerializedName;

public class SearchVenuesResponse {
	@SerializedName("response")
	private Response response_;
	public Venue[] getVenues() {
		return response_.getVenues();
	}

	public void setVenues(Venue[] venues_) {
		response_.setVenues(venues_);
	}

	public static class Response{
		@SerializedName("venues")
		private Venue[] venues_;

		public Venue[] getVenues() {
			return venues_;
		}

		public void setVenues(Venue[] venues_) {
			this.venues_ = venues_;
		}
	}
}

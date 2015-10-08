package com.weezlabs.forsquarelib.models;

import com.google.gson.annotations.SerializedName;

public class Hours {
	@SerializedName("status")
	private String status_;
	@SerializedName("isOpen")
	private boolean isOpen_;
	@SerializedName("timeframes")
	private Timeframe[] timeframes_;
	@SerializedName("segments")
	private Segment[] segments_;

	public String getStatus() {
		return status_;
	}

	public void setStatus(String status_) {
		this.status_ = status_;
	}

	public boolean isOpen() {
		return isOpen_;
	}

	public void setIsOpen(boolean isOpen_) {
		this.isOpen_ = isOpen_;
	}

	public Timeframe[] getTimeframes() {
		return timeframes_;
	}

	public void setTimeframes(Timeframe[] timeframes_) {
		this.timeframes_ = timeframes_;
	}

	public Segment[] getSegments() {
		return segments_;
	}

	public void setSegments(Segment[] segments_) {
		this.segments_ = segments_;
	}

	public static class Timeframe{
		@SerializedName("days")
		private String[] days_;
		@SerializedName("open")
		private String[] open_;
		@SerializedName("renderedTime")
		private String renderedTime_;

		public String[] getDays() {
			return days_;
		}

		public void setDays(String[] days_) {
			this.days_ = days_;
		}

		public String[] getOpen() {
			return open_;
		}

		public void setOpen(String[] open_) {
			this.open_ = open_;
		}

		public String getRenderedTime() {
			return renderedTime_;
		}

		public void setRenderedTime(String renderedTime_) {
			this.renderedTime_ = renderedTime_;
		}
	}

	public static class Segment{
		@SerializedName("lable")
		private String lable_;
		@SerializedName("renderedTime")
		private String renderedTime_;

		public String getLable() {
			return lable_;
		}

		public void setLable(String lable_) {
			this.lable_ = lable_;
		}

		public String getRenderedTime() {
			return renderedTime_;
		}

		public void setRenderedTime(String renderedTime) {
			this.renderedTime_ = renderedTime;
		}
	}
}

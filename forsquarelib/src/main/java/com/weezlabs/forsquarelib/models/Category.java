package com.weezlabs.forsquarelib.models;

import com.google.gson.annotations.SerializedName;

public class Category {
	@SerializedName("id")
	String id_;
	@SerializedName("name")
	String name_;
	@SerializedName("pluralName")
	String pluralName_;
	@SerializedName("shortName")
	String shortName_;

	public String getId() {
		return id_;
	}

	public void setId(String id_) {
		this.id_ = id_;
	}

	public String getName() {
		return name_;
	}

	public void setName(String name_) {
		this.name_ = name_;
	}

	public String getPluralName() {
		return pluralName_;
	}

	public void setPluralName(String pluralName_) {
		this.pluralName_ = pluralName_;
	}

	public String getShortName() {
		return shortName_;
	}

	public void setShortName(String shortName_) {
		this.shortName_ = shortName_;
	}
}

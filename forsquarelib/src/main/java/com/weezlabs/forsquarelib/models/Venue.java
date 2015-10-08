package com.weezlabs.forsquarelib.models;

import com.google.gson.annotations.SerializedName;

public class Venue {
	@SerializedName("id")
	private String id_;
	@SerializedName("name")
	private String name_;
	@SerializedName("categories")
	private Category[] categories_;
	@SerializedName("hours")
	private Hours hours_;

	@SerializedName("menu")
	private String menu_;
	@SerializedName("price")
	private String price_;
	@SerializedName("rating")
	private String rating_;
	@SerializedName("description")
	private String description_;
	@SerializedName("tags")
	private String[] tags_;

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

	public Category[] getCategories() {
		return categories_;
	}

	public void setCategories(Category[] categories_) {
		this.categories_ = categories_;
	}

	public Hours getHours() {
		return hours_;
	}

	public void setHours(Hours hours_) {
		this.hours_ = hours_;
	}

	public String getMenu() {
		return menu_;
	}

	public void setMenu(String menu_) {
		this.menu_ = menu_;
	}

	public String getPrice() {
		return price_;
	}

	public void setPrice(String price_) {
		this.price_ = price_;
	}

	public String getRating() {
		return rating_;
	}

	public void setRating(String rating_) {
		this.rating_ = rating_;
	}

	public String getDescription() {
		return description_;
	}

	public void setDescription(String description_) {
		this.description_ = description_;
	}

	public String[] getTags() {
		return tags_;
	}

	public void setTags(String[] tags_) {
		this.tags_ = tags_;
	}
}

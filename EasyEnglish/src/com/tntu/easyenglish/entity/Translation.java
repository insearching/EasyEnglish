package com.tntu.easyenglish.entity;

public class Translation {
	private String text;
	private String imageUrl;
	
	public Translation(String text, String imageUrl) {
		this.text = text;
		this.imageUrl = imageUrl;
	}

	public String getText() {
		return text;
	}

	public String getImageUrl() {
		return imageUrl;
	}


}

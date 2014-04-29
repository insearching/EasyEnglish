package com.tntu.easyenglish.entity;

public class Exercise {

	private int id;
	private String phrase;
	private String voiceLink;
	private String pictureLink;
	
	public Exercise(int id, String phrase, String voiceLink, String pictureLink) {
		super();
		this.id = id;
		this.phrase = phrase;
		this.voiceLink = voiceLink;
		this.pictureLink = pictureLink;
	}

	public int getId() {
		return id;
	}

	public String getPhrase() {
		return phrase;
	}

	public String getVoiceLink() {
		return voiceLink;
	}

	public String getPictureLink() {
		return pictureLink;
	}
}

package com.tntu.easyenglish.entity;

import java.io.Serializable;

public class SoundToWord implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String phrase;
	private String pictureLink;
	private String voiceLink;

	public SoundToWord(int id, String phrase, String pictureLink,
			String voiceLink) {
		super();
		this.id = id;
		this.phrase = phrase;
		this.pictureLink = pictureLink;
		this.voiceLink = voiceLink;
	}

	public int getId() {
		return id;
	}

	public String getPhrase() {
		return phrase;
	}

	public String getPictureLink() {
		return pictureLink;
	}

	public String getVoiceLink() {
		return voiceLink;
	}
}

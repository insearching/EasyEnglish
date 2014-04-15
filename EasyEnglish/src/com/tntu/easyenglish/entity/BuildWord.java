package com.tntu.easyenglish.entity;

import java.io.Serializable;

public class BuildWord implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private String phrase;
	private String translation;
	private String pictureLink;
	private String voiceLink;
	private char[] symbols;

	public BuildWord(int id, String phrase, String translation,
			String pictureLink, String voiceLink, char[] symbols) {
		super();
		this.id = id;
		this.phrase = phrase;
		this.translation = translation;
		this.pictureLink = pictureLink;
		this.voiceLink = voiceLink;
		this.symbols = symbols;
	}

	public int getId() {
		return id;
	}

	public String getPhrase() {
		return phrase;
	}

	public String getTranslation() {
		return translation;
	}

	public String getPictureLink() {
		return pictureLink;
	}

	public String getVoiceLink() {
		return voiceLink;
	}

	public char[] getSymbols() {
		return symbols;
	}
}

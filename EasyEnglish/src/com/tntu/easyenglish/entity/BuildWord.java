package com.tntu.easyenglish.entity;

import java.io.Serializable;

public class BuildWord extends Exercise implements Serializable{

	private static final long serialVersionUID = 1L;
	private String translation;
	private char[] symbols;

	public BuildWord(int id, String phrase, String translation,
			String pictureLink, String voiceLink, char[] symbols) {
		super(id, phrase, voiceLink, pictureLink);
		this.translation = translation;
		this.symbols = symbols;
	}

	public String getTranslation() {
		return translation;
	}

	public char[] getSymbols() {
		return symbols;
	}
}

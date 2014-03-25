package com.tntu.easyenglish.entity;

public class DictionaryWord {
	private int dictionaryId;
	private int wordId;
	private String word;
	private String[] translations;
	private String[] contexts;
	private String[] images;
	private String sound;
	private String date;
	
	public DictionaryWord(int dictionaryId, int wordId, String word,
			String[] translations, String[] contexts, String[] images,
			String sound, String date) {
		super();
		this.dictionaryId = dictionaryId;
		this.wordId = wordId;
		this.word = word;
		this.translations = translations;
		this.contexts = contexts;
		this.images = images;
		this.sound = sound;
		this.date = date;
	}

	public int getDictionaryId() {
		return dictionaryId;
	}

	public int getWordId() {
		return wordId;
	}

	public String getWord() {
		return word;
	}

	public String[] getTranslations() {
		return translations;
	}

	public String[] getContexts() {
		return contexts;
	}

	public String[] getImages() {
		return images;
	}

	public String getSound() {
		return sound;
	}

	public String getDate() {
		return date;
	}

}

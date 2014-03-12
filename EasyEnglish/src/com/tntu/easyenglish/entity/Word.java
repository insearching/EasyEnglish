package com.tntu.easyenglish.entity;

public class Word {
	public enum Status {
		UNKNOWN, QUARTER, HALF, HALF_QUARTER, STUDIED
	}
	private int id;
	private String word;
	private String translation;
	private Status status;
	
	public Word(int id, String word, String translation, Status status){
		this.id = id;
		this.word = word;
		this.translation = translation;
		this.status = status;
	}
	
	public int getId() {
		return id;
	}
	
	public String getWord() {
		return word;
	}
	public String getTranslation() {
		return translation;
	}
	
	public Status getStatus(){
		return status;
	}
}

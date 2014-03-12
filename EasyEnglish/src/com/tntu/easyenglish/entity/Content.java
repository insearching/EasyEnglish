package com.tntu.easyenglish.entity;

public class Content {
	private int id;
	private String title;
	private String genre;
	private int level;
	private String date;
	
	public Content(int id, String title, String genre, int level, String date) {
		super();
		this.id = id;
		this.title = title;
		this.genre = genre;
		this.level = level;
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getGenre() {
		return genre;
	}
	public int getLevel() {
		return level;
	}
	public String getDate() {
		return date;
	}
}

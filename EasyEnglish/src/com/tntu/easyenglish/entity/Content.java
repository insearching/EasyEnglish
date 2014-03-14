package com.tntu.easyenglish.entity;

public class Content {
	private int id;
	private String title;
	private int ownerId;
	private int type;
	private String genre;
	private String text;
	private int level;
	private int pages;
	private String playerLink;
	private String date;
	
	public Content(int id, String title, String genre, int type, int level, String date) {
		super();
		this.id = id;
		this.title = title;
		this.genre = genre;
		this.type = type;
		this.level = level;
		this.date = date;
	}
	

	public Content(int id, String title, int ownerId, int type,
			String genre, String text, int level, int pages, String playerLink,
			String date) {
		super();
		this.id = id;
		this.title = title;
		this.ownerId = ownerId;
		this.type = type;
		this.genre = genre;
		this.text = text;
		this.level = level;
		this.pages = pages;
		this.playerLink = playerLink;
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


	public int getOwnerId() {
		return ownerId;
	}

	public int getType() {
		return type;
	}


	public String getText() {
		return text;
	}


	public int getPages() {
		return pages;
	}


	public String getPlayerLink() {
		return playerLink;
	}
}

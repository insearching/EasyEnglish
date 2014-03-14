package com.tntu.easyenglish.entity;

public class User {
	private String login;
	private String email;
	private String regDate;

	public User(String login, String email, String regDate) {
		this.login = login;
		this.email = email;
		this.regDate = regDate;
	}

	public String getLogin() {
		return login;
	}

	public String getEmail() {
		return email;
	}

	public String getDate() {
		return regDate;
	}

}

package com.tntu.easyenglish.entity;

public class User {
	private String login;
	private String email;
	private String avatar;
	private String regDate;

	public User(String login, String email, String avatar, String regDate) {
		this.login = login;
		this.email = email;
		this.avatar = avatar;
		this.regDate = regDate;
	}

	public String getLogin() {
		return login;
	}

	public String getEmail() {
		return email;
	}
	
	public String getAvatar(){
		return avatar;
	}

	public String getDate() {
		return regDate;
	}

}

package com.tntu.easyenglish.entity;

import java.io.Serializable;
import java.util.HashMap;

public class WordTrans implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private String phrase;
	private String pictureLink;
	private String context;
	private String voiceLink;
	private int correctAnswer;

//	private Answer[] answers;
	
	private HashMap<String, Integer> answers;
	
	public static class Answer {
		public int id;
		public String phrase;
	}

	public WordTrans(int id, String phrase, String pictureLink, String context,
			String voiceLink, int correctAnswer, HashMap<String, Integer> answers) {
		super();
		this.id = id;
		this.phrase = phrase;
		this.pictureLink = pictureLink;
		this.context = context;
		this.voiceLink = voiceLink;
		this.correctAnswer = correctAnswer;
		this.answers = answers;
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

	public String getContext() {
		return context;
	}


	public String getVoiceLink() {
		return voiceLink;
	}
	
	public int getCorrectAnswer(){
		return correctAnswer;
	}
	
	public HashMap<String, Integer> getAnswers(){
		return answers;
	}
}

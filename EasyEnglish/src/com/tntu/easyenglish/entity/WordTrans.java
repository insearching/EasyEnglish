package com.tntu.easyenglish.entity;

import java.io.Serializable;
import java.util.HashMap;

public class WordTrans extends Exercise implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String context;
	private int correctAnswer;
	
	private HashMap<String, Integer> answers;
	
	public static class Answer {
		public int id;
		public String phrase;
	}

	public WordTrans(int id, String phrase, String pictureLink, String context,
			String voiceLink, int correctAnswer, HashMap<String, Integer> answers) {
		super(id, phrase, voiceLink, pictureLink);
		this.context = context;
		this.correctAnswer = correctAnswer;
		this.answers = answers;
	}

	public String getContext() {
		return context;
	}
	
	public int getCorrectAnswer(){
		return correctAnswer;
	}
	
	public HashMap<String, Integer> getAnswers(){
		return answers;
	}
}

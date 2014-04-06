package com.tntu.easyenglish.entity;

public class WordTrans {

	private int id;
	private String phrase;
	private String pictureLink;
	private String context;
	private String voiceLink;
	private int correctAnswer;

	private Answer[] answers;
	
	public static class Answer {
		public int id;
		public String phrase;
	}

	public WordTrans(int id, String phrase, String pictureLink, String context,
			String voiceLink, int correctAnswer, Answer[] answers) {
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
	
	public Answer[] getAnswers(){
		return answers;
	}
}

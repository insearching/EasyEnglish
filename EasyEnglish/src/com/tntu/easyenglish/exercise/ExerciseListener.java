package com.tntu.easyenglish.exercise;

public interface ExerciseListener {
	public void onTestCompleted(Integer exerciseId, boolean isCorrect, String type);
}

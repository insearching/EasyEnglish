package com.tntu.easyenglish.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.WordTrans.Answer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AnswersAdapter extends BaseAdapter {

	private HashMap<String, Integer> answers;
	private Context context;
	private ArrayList<String> phrases;
	
	private int correctPosition = -1;
	private int wrongPosition = -1;
	
	public AnswersAdapter(Context context, HashMap<String, Integer> answers) {
		this.context = context;
		this.answers = answers;
		phrases = new ArrayList<String>();
		phrases.addAll(answers.keySet());
	}

	@Override
	public int getCount() {
		return phrases.size();
	}
	
	
	@Override
	public Answer getItem(int position) {
		Answer answer = new Answer();
		answer.id = answers.get(phrases.get(position));
		answer.phrase = phrases.get(position);
		return answer;
	}

	@Override
	public long getItemId(int position) {
		return answers.get(phrases.get(position));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		convertView = inflater.inflate(R.layout.answer_row_layout, parent, false);
		TextView tv = (TextView)convertView.findViewById(R.id.answerTv);
		tv.setText(phrases.get(position));
		
		if(correctPosition != -1)
			convertView.setBackgroundColor(Color.GREEN);
		if(wrongPosition != -1)
			convertView.setBackgroundColor(Color.RED);
		return convertView;
	}
	
	public void setCorrectAnswer(int position){
		correctPosition = position;
	}
	
	public void setWrongAnswer(int position){
		wrongPosition = position;
	}
}

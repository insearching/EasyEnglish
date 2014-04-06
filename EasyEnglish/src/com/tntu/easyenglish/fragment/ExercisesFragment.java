package com.tntu.easyenglish.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tntu.easyenglish.ExercisesActivity;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.KeyUtils.ExerciseType;

public class ExercisesFragment extends Fragment implements OnClickListener{
	private View convertView;

	public static ExercisesFragment newInstance(String apiKey) {
		ExercisesFragment fragment = new ExercisesFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		fragment.setArguments(args);
		return fragment;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.exercises_fragment, null);
		((TextView)convertView.findViewById(R.id.wordTransTv)).setOnClickListener(this);
		((TextView)convertView.findViewById(R.id.transWordTv)).setOnClickListener(this);
		((TextView)convertView.findViewById(R.id.wordConstrTv)).setOnClickListener(this);
		((TextView)convertView.findViewById(R.id.listeningTv)).setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = new Intent(getActivity(), ExercisesActivity.class);
		ExerciseType type = ExerciseType.WORD_TRANSLATION;
		switch (id) {
		case R.id.wordTransTv:
			type = ExerciseType.WORD_TRANSLATION;
			break;
			
		case R.id.transWordTv:
			type = ExerciseType.TRANSLATION_WORD;
			break;
			
		case R.id.wordConstrTv:
			type = ExerciseType.WORD_CONSTRUCTOR;
			break;
			
		case R.id.listeningTv:
			type = ExerciseType.LISTENING;
			break;
		default:
			break;
		}
		
		intent.putExtra(KeyUtils.EXERCISES_TYPE_KEY, type);
		intent.putExtra(KeyUtils.API_KEY, getArguments().getString(KeyUtils.API_KEY));
		startActivity(intent);
	}
}

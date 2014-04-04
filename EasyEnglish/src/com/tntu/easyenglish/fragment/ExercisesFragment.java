package com.tntu.easyenglish.fragment;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExercisesFragment extends Fragment{
	private View convertView;
	
	public static ExercisesFragment newInstance(String json) {
		ExercisesFragment fragment = new ExercisesFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.JSON_KEY, json);
		fragment.setArguments(args);
		return fragment;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.exercises_fragment, null);
		return convertView;
	}
}

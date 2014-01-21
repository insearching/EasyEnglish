package com.tntu.easyenglish.fragment;

import com.tntu.easyenglish.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExercisesFragment extends Fragment {
	private View convertView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.exercises_fragment, null);
		return convertView;
	}
}

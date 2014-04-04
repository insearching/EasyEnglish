package com.tntu.easyenglish.fragment;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExercisesListFragment extends Fragment implements JSONCompleteListener{
	private View convertView;
	
	public static ExercisesListFragment newInstance(String apiKey) {
		ExercisesListFragment fragment = new ExercisesListFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		fragment.setArguments(args);
		return fragment;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.exercises_fragment, null);
		return convertView;
	}

	@Override
	public void onRemoteCallComplete(String json) {
		
	}
}

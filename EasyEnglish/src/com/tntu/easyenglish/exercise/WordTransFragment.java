package com.tntu.easyenglish.exercise;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.utils.KeyUtils;

public class WordTransFragment extends Fragment {
	
	private View convertView;
	public static WordTransFragment newInstance(String apiKey) {
		WordTransFragment fragment = new WordTransFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.word_trans_fragment, null, false);
		return convertView;
	}
}

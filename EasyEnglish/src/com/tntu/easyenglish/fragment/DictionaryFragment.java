package com.tntu.easyenglish.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.DictionaryAdapter;
import com.tntu.easyenglish.entity.DictionaryWord;
import com.tntu.easyenglish.utils.ContentCacheLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class DictionaryFragment extends Fragment implements
		JSONCompleteListener {
	private View convertView;
	private ListView dictionaryLv;
	private ProgressBar loadPb;
	private DictionaryAdapter mAdapter;
	private ContentCacheLoader loader;

	private static final int mCount = 10;
	private static final String bufferFileName = "dictionary.txt";

	public static DictionaryFragment newInstance(String apiKey) {
		DictionaryFragment fragment = new DictionaryFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.dictionary_fragment, null);
		
		dictionaryLv = (ListView) convertView.findViewById(R.id.dictionaryLv);
		loadPb = (ProgressBar) convertView.findViewById(R.id.loadPb);
		setHasOptionsMenu(true);

		loader = new ContentCacheLoader(getActivity());
		String json = loader.readFromFile(bufferFileName);
		if (json != null && !json.equals("")) {
			setData(json);
		} else {
			String apiKey = getArguments().getString(KeyUtils.API_KEY);
			RESTClient client = new RESTClient(this);
			client.execute(getQuery(apiKey));
		}

		return convertView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("TAG", ""+item.getTitle());
		switch (item.getItemId()) {
		case R.id.refresh:
			refreshContentList();
		}
		return true;
	}

	public void refreshContentList() {
		if (loader == null)
			return;
		loader.deleteFile(bufferFileName);
		hideView();

		String apiKey = getArguments().getString(KeyUtils.API_KEY);
		RESTClient client = new RESTClient(this);
		client.execute(getQuery(apiKey));
	}

	private void setData(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			ArrayList<DictionaryWord> dictionary = JSONUtils
					.getUserDictionary(json);
			mAdapter = new DictionaryAdapter(getActivity(), dictionary);
			dictionaryLv.setAdapter(mAdapter);
			loader.writeToFile(bufferFileName, json);
			
			showView();
		}
	}

	@Override
	public void onRemoteCallComplete(String json) {
		setData(json);
	}

	private void hideView() {
		dictionaryLv.setVisibility(View.GONE);
		loadPb.setVisibility(View.VISIBLE);
	}

	private void showView() {
		dictionaryLv.setVisibility(View.VISIBLE);
		loadPb.setVisibility(View.GONE);
	}

	private String getQuery(String apiKey) {
		return "http://easy-english.yzi.me/api/getUserDictionary?api_key="
				+ apiKey + "&count=" + mCount + "&offset=0";
	}
}

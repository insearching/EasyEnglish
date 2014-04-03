package com.tntu.easyenglish.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
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
	private PullAndLoadListView contentLv;
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
		
		contentLv = (PullAndLoadListView) convertView.findViewById(R.id.contentLv);
		contentLv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshContentList();
				
			}
		});
		
		contentLv.setOnLoadMoreListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				refreshContentList();
			}
		});
		setHasOptionsMenu(true);

		loader = new ContentCacheLoader(getActivity());
		String json = loader.readFromFile(bufferFileName);
		if (json != null && !json.equals("")) {
			setData(json);
		} else {
			refreshContentList();
		}

		return convertView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public void refreshContentList() {
		if (loader == null)
			return;
		loader.deleteFile(bufferFileName);

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
			contentLv.setAdapter(mAdapter);
			loader.writeToFile(bufferFileName, json);
		}
	}

	@Override
	public void onRemoteCallComplete(String json) {
		contentLv.onLoadMoreComplete();
		contentLv.onRefreshComplete();
		setData(json);
	}

	private String getQuery(String apiKey) {
		return "http://easy-english.yzi.me/api/getUserDictionary?api_key="
				+ apiKey + "&count=" + mCount + "&offset=0";
	}
}

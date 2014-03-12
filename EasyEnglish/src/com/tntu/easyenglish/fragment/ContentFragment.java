package com.tntu.easyenglish.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tntu.easyenglish.LoginActivity;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.ContentListAdapter;
import com.tntu.easyenglish.entity.Content;
import com.tntu.easyenglish.utils.ContentCacheLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class ContentFragment extends Fragment implements JSONCompleteListener {
	private View convertView;
	private ListView contentLv;
	private ContentListAdapter mAdapter;
	private RESTClient client;
	private String requestUrl;
	private static ContentFragment instance;
	private ContentCacheLoader loader;
	private static final String fileName = "content.txt";

	public static ContentFragment newInstance(String apiKey) {
		instance = new ContentFragment();
		Bundle args = new Bundle();
		args.putString(LoginActivity.API_KEY, apiKey);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.content_fragment, null);
		contentLv = (ListView) convertView.findViewById(R.id.contentLv);

		loader = new ContentCacheLoader(getActivity());
		String info = loader.readFromFile(fileName);
		if (!info.equals("")) {
			setContentList(info);
		} else {
			String apiKey = getArguments().getString(LoginActivity.API_KEY);
			requestUrl = "http://easy-english.yzi.me/api/getContentsList?api_key="
					+ apiKey + "&count=10";
			client = new RESTClient(this);
			client.execute(requestUrl);
		}
		return convertView;
	}

	@Override
	public void onRemoteCallComplete(String json) {
		client = new RESTClient(this);
		setContentList(json);
		loader.writeToFile(fileName, json);
	}
	
	private void setContentList(String json){
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			ArrayList<Content> contentList = JSONUtils.getContentList(json);
			mAdapter = new ContentListAdapter(getActivity(), contentList);
			contentLv.setAdapter(mAdapter);
		}
	}
}

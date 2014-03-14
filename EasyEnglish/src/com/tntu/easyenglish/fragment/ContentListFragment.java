package com.tntu.easyenglish.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.ContentListAdapter;
import com.tntu.easyenglish.entity.Content;
import com.tntu.easyenglish.utils.ContentCacheLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class ContentListFragment extends Fragment implements
		JSONCompleteListener, OnItemClickListener {
	private View convertView;
	private ListView contentLv;
	private ContentListAdapter mAdapter;
	private RESTClient client;
	private String requestUrl;
	private String apiKey = null;
	private static ContentListFragment instance;
	private ContentCacheLoader loader;
	private static final String fileName = "content_list.txt";

	public static ContentListFragment newInstance(String apiKey) {
		instance = new ContentListFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.content_list_fragment, null);
		contentLv = (ListView) convertView.findViewById(R.id.contentLv);
		contentLv.setOnItemClickListener(this);

		loader = new ContentCacheLoader(getActivity());
		String info = loader.readFromFile(fileName);
		if (!info.equals("")) {
			setContentList(info);
		} else {
			apiKey = getArguments().getString(KeyUtils.API_KEY);
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

	private void setContentList(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			ArrayList<Content> contentList = JSONUtils.getContentList(json);
			mAdapter = new ContentListAdapter(getActivity(), contentList);
			contentLv.setAdapter(mAdapter);
		}
	}

	public void refreshContentList() {
		if (loader == null)
			return;
		loader.deleteFile(fileName);
		client = new RESTClient(this);
		client.execute(requestUrl);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View convertView,
			int position, long id) {
		int sId = mAdapter.getItem(position).getId();
		ContentFragment fragment = ContentFragment.newInstance(getArguments().getString(KeyUtils.API_KEY), sId);
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.float_left_to_right_in_anim,
						R.anim.float_left_to_right_out_anim)
				.replace(R.id.content_frame, fragment)
				.addToBackStack("content_list").commit();
	}
}

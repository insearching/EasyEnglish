package com.tntu.easyenglish.fragment;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.YoutubeActivity;
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
	private ProgressBar loadPb;
	private ContentListAdapter mAdapter;
	private RESTClient client;
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
		loadPb = (ProgressBar) convertView.findViewById(R.id.loadPb);

		loader = new ContentCacheLoader(getActivity());
		String info = loader.readFromFile(fileName);
		if (!info.equals("")) {
			setContentList(info);
		} else {
			hideList();
			String apiKey = getArguments().getString(KeyUtils.API_KEY);
			String requestUrl = "http://easy-english.yzi.me/api/getContentsList?api_key="
					+ apiKey + "&count=10";
			client = new RESTClient(this);
			client.execute(requestUrl);
		}
		return convertView;
	}

	@Override
	public void onRemoteCallComplete(String json) {
		client = new RESTClient(this);

		if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_TRUE)) {
			if (json.contains(KeyUtils.PLAYER_LINK_KEY)) {
				showVideoActivity(json);
			} else {
				setContentList(json);
				loader.writeToFile(fileName, json);
			}
			showList();
		}
	}

	private void showVideoActivity(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			Content content = JSONUtils.getContentData(json);
			String playerLink = content.getPlayerLink();
			String text = content.getText();

			if (playerLink != null && text != null) {
				String videoId = getVideoId(playerLink);

				if (videoId != null) {
					Intent intent = new Intent(getActivity(),
							YoutubeActivity.class);
					intent.putExtra(KeyUtils.VIDEO_ID_KEY, videoId);
					intent.putExtra(KeyUtils.TEXT_KEY, text);
					startActivity(intent);
				}
			}
		}
	}

	private String getVideoId(String url) {
		String videoId = null;
		Pattern pattern = Pattern.compile("/embed/([\\w\\.-]*)");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find())
			videoId = matcher.group(1);
		return videoId;
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
		hideList();
		String apiKey = getArguments().getString(KeyUtils.API_KEY);
		String requestUrl = "http://easy-english.yzi.me/api/getContentsList?api_key="
				+ apiKey + "&count=10";
		client = new RESTClient(this);
		client.execute(requestUrl);
	}

	private void showList() {
		loadPb.setVisibility(View.GONE);
		contentLv.setVisibility(View.VISIBLE);
	}

	private void hideList() {
		loadPb.setVisibility(View.VISIBLE);
		contentLv.setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View convertView,
			int position, long id) {
		int sId = mAdapter.getItem(position).getId();
		int type = mAdapter.getItem(position).getType();

		if (type == KeyUtils.VIDEO_TYPE_KEY) {
			String apiKey = getArguments().getString(KeyUtils.API_KEY);
			String requestUrl = "http://easy-english.yzi.me/api/getContentData?api_key="
					+ apiKey + "&id=" + id;
			client = new RESTClient(this);
			client.execute(requestUrl);
			hideList();
		} else {
			ContentFragment fragment = ContentFragment.newInstance(
					getArguments().getString(KeyUtils.API_KEY), sId);
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.float_left_to_right_in_anim,
							R.anim.float_left_to_right_out_anim)
					.replace(R.id.content_frame, fragment, KeyUtils.CONTENT_TAG)
					.addToBackStack("content_list").commit();
		}

	}
}

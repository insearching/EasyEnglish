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

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.tntu.easyenglish.ContentActivity;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.YoutubeActivity;
import com.tntu.easyenglish.adapter.ContentListAdapter;
import com.tntu.easyenglish.entity.Content;
import com.tntu.easyenglish.utils.ContentCacheLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListenerMethod;

public class ContentListFragment extends Fragment implements
		JSONCompleteListenerMethod, OnItemClickListener {
	private View convertView;
	private PullAndLoadListView contentLv;
	private ContentListAdapter mAdapter;
	private ContentCacheLoader loader;
	private static final String bufferFileName = "content_list.txt";
	private static final String GET_LIST_METHOD = "getList";
	private static final String GET_CONTENT_METHOD = "getContent";
	private static final int COUNT = 10;
	private static int offset = 0;

	public static ContentListFragment newInstance(String apiKey) {
		ContentListFragment fragment = new ContentListFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.content_list_fragment, null);
		contentLv = (PullAndLoadListView) convertView
				.findViewById(R.id.contentLv);
		contentLv.setOnItemClickListener(this);
		contentLv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshContentList();
			}
		});

		contentLv.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {

			}
		});

		loader = new ContentCacheLoader(getActivity());

		String info = null;
		if (loader.isFileExists(bufferFileName))
			info = loader.readFromFile(bufferFileName);

		if (info != null && !info.equals("")) {
			setData(info);
		} else {
			refreshContentList();
		}
		return convertView;
	}

	@Override
	public void onRemoteCallComplete(String json, String method) {
		contentLv.onRefreshComplete();
		if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_TRUE)) {
			if (method.equals(GET_CONTENT_METHOD)) {
				startContentActivity(json);
			} else if (method.equals(GET_LIST_METHOD)) {
				contentLv.onRefreshComplete();
				setData(json);
			}
		}
	}

	private void startContentActivity(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			Content content = JSONUtils.getContentData(json);
			String title = content.getTitle();
			String playerLink = content.getPlayerLink();
			String text = content.getText();
			String apiKey = getArguments().getString(KeyUtils.API_KEY);

			Intent intent = null;
			if (playerLink != null && text != null) {
				String videoId = getVideoId(playerLink);
				if (videoId != null) {
					intent = new Intent(getActivity(), YoutubeActivity.class);
					intent.putExtra(KeyUtils.VIDEO_ID_KEY, videoId);
				}
			} else {
				intent = new Intent(getActivity(), ContentActivity.class);
			}
			intent.putExtra(KeyUtils.TITLE_KEY, title);
			intent.putExtra(KeyUtils.TEXT_KEY, text);
			intent.putExtra(KeyUtils.API_KEY, apiKey);
			startActivity(intent);
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

	private void setData(String json) {
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
		loader.deleteFile(bufferFileName);
		String apiKey = getArguments().getString(KeyUtils.API_KEY);
		
		RESTClient client = new RESTClient(this, GET_LIST_METHOD);
		client.execute(getQuery(apiKey, 0));
		offset = COUNT;
	}

	public void loadMoreContent() {
		RESTClient client = new RESTClient(this, GET_LIST_METHOD);
		client.execute(getQuery(getArguments().getString(KeyUtils.API_KEY),
				offset));
		offset += COUNT;
	}

	private String getQuery(String apiKey, int offset) {
		return "http://easy-english.yzi.me/api/getContentsList?api_key="
				+ apiKey + "&count=" + COUNT + "&offset=" + offset;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View convertView,
			int position, long id) {
		position -= 1;
		int sId = mAdapter.getItem(position).getId();

		String apiKey = getArguments().getString(KeyUtils.API_KEY);
		String requestUrl = "http://easy-english.yzi.me/api/getContentData?api_key="
				+ apiKey + "&id=" + sId;
		RESTClient client = new RESTClient(this, GET_CONTENT_METHOD);
		client.execute(requestUrl);
	}
}

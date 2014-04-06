package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.Content;
import com.tntu.easyenglish.utils.ContentCacheLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;
import com.tntu.easyenglish.view.TranslationDialog;

public class ContentFragment extends Fragment implements JSONCompleteListener {
	private View convertView;
	private LinearLayout contentLl;
	private ProgressBar loadPb;
	private TextView contentTv;
	
	private RESTClient client;
	private String requestUrl;
	private ContentCacheLoader loader;
	private static final String fileName = "content.txt";

	public static ContentFragment newInstance(String apiKey, int id) {
		ContentFragment fragment = new ContentFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		args.putInt(KeyUtils.ID_KEY, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.content_fragment, null, false);

		contentTv = (TextView) convertView.findViewById(R.id.contentTv);
		contentLl = (LinearLayout) convertView.findViewById(R.id.contentLl);
		loadPb = (ProgressBar) convertView.findViewById(R.id.loadPb);
		loader = new ContentCacheLoader(getActivity());
		

		String info = loader.readFromFile(fileName);
		int id = getArguments().getInt(KeyUtils.ID_KEY);
		if (!info.equals("") && JSONUtils.getContentId(info) == id) {
			showData(info);
		} else {
			String apiKey = getArguments().getString(KeyUtils.API_KEY);
			requestUrl = "http://easy-english.yzi.me/api/getContentData?api_key="
					+ apiKey + "&id=" + id;
			client = new RESTClient(this);
			client.execute(requestUrl);
			hideView();
		}
		return convertView;
	}

	@Override
	public void onRemoteCallComplete(String json) {
		client = new RESTClient(this);
		showData(json);
		showView();
		loader.writeToFile(fileName, json);
	}

	private void showData(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			Content content = JSONUtils.getContentData(json);
			String apiKey = getArguments().getString(KeyUtils.API_KEY);
			TranslationDialog dialog = new TranslationDialog(getActivity(), apiKey);
			dialog.initContentText(contentTv, content.getText().trim());
		}
	}

	public void refreshContentList() {
		if (loader == null)
			return;
		loader.deleteFile(fileName);
		String apiKey = getArguments().getString(KeyUtils.API_KEY);
		int id = getArguments().getInt(KeyUtils.ID_KEY);
		String requestUrl = "http://easy-english.yzi.me/api/getContentData?api_key="
				+ apiKey + "&id=" + id;
		client = new RESTClient(this);
		client.execute(requestUrl);
		hideView();
	}

	private void hideView() {
		loadPb.setVisibility(View.VISIBLE);
		contentLl.setVisibility(View.GONE);
	}

	private void showView() {
		loadPb.setVisibility(View.GONE);
		contentLl.setVisibility(View.VISIBLE);
	}
}

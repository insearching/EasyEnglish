package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.Content;
import com.tntu.easyenglish.utils.ContentCacheLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class ContentFragment extends Fragment implements JSONCompleteListener {
	private View convertView;
	private WebView playerWv;
	private TextView contentTv;
	private RESTClient client;
	private String requestUrl;
	private static ContentFragment instance;
	private ContentCacheLoader loader;
	private static final String fileName = "content.txt";

	public static ContentFragment newInstance(String apiKey, int id) {
		instance = new ContentFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		args.putInt(KeyUtils.ID_KEY, id);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.content_fragment, null);
		playerWv = (WebView) convertView.findViewById(R.id.playerWv);
		contentTv = (TextView) convertView.findViewById(R.id.contentTv);
		loader = new ContentCacheLoader(getActivity());

		String info = loader.readFromFile(fileName);
		int id = getArguments().getInt(KeyUtils.ID_KEY);
		if (!info.equals("") && JSONUtils.getContentId(info) == id) {
			setData(info);
		} else {
			String apiKey = getArguments().getString(KeyUtils.API_KEY);
			requestUrl = "http://easy-english.yzi.me/api/getContentData?api_key="
					+ apiKey + "&id=" + id;
			client = new RESTClient(this);
			client.execute(requestUrl);
		}
		return convertView;
	}

	@Override
	public void onRemoteCallComplete(String json) {
		client = new RESTClient(this);
		setData(json);
		loader.writeToFile(fileName, json);
	}

	private void setData(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			Content content = JSONUtils.getContentData(json);
			String playerLink = content.getPlayerLink();
			if (playerLink != null && !playerLink.equals("null")) {
				playerWv.getSettings().setJavaScriptEnabled(true);
				playerWv.loadDataWithBaseURL("", content.getPlayerLink(),
						"text/html", "UTF-8", "");
			}
			else {
				playerWv.setVisibility(View.GONE);
			}
			contentTv.setText(content.getText());
		}
	}

	public void refreshContentList() {
		if (loader == null)
			return;
		loader.deleteFile(fileName);
		client = new RESTClient(this);
		client.execute(requestUrl);
	}
}

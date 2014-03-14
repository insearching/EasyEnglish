package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.Content;
import com.tntu.easyenglish.utils.ContentCacheLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class ContentFragment extends YouTubePlayerSupportFragment implements JSONCompleteListener,
		YouTubePlayer.OnInitializedListener {
	private View convertView;
	// private WebView playerWv;
	private LinearLayout contentLl;
	private ProgressBar loadPb;
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
		// playerWv = (WebView) convertView.findViewById(R.id.playerWv);

		YouTubePlayerView youTubeView = (YouTubePlayerView) convertView
				.findViewById(R.id.youtube_api_demo);
		youTubeView.initialize(KeyUtils.DEVELOPER_KEY, this);

		contentTv = (TextView) convertView.findViewById(R.id.contentTv);
		contentLl = (LinearLayout) convertView.findViewById(R.id.contentLl);
		loadPb = (ProgressBar) convertView.findViewById(R.id.loadPb);
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
			hideView();
		}
		return convertView;
	}

	@Override
	public void onRemoteCallComplete(String json) {
		client = new RESTClient(this);
		setData(json);
		showView();
		loader.writeToFile(fileName, json);
	}

	private void setData(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			Content content = JSONUtils.getContentData(json);
			String playerLink = content.getPlayerLink();
			if (playerLink != null && !playerLink.equals("null")) {
				// playerWv.getSettings().setJavaScriptEnabled(true);
				// playerLink =
				// "<iframe src=\"http://www.youtube.com/embed/71m94sRcVDo\" frameborder=\"0\" allowfullscreen></iframe>";
				// playerWv.loadDataWithBaseURL("", playerLink, "text/html",
				// "UTF-8", "");
			} else {
				// playerWv.setVisibility(View.GONE);
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
		hideView();
	}

	private void hideView() {
		loadPb.setVisibility(View.GONE);
		contentLl.setVisibility(View.VISIBLE);
	}

	private void showView() {
		loadPb.setVisibility(View.VISIBLE);
		contentLl.setVisibility(View.GONE);
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {

	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo("wKJ9KzGQq0w");
		}

	}
}

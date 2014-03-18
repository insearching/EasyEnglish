package com.tntu.easyenglish;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.view.ViewInitializer;

public class YoutubeActivity extends YouTubeBaseActivity implements
		YouTubePlayer.OnInitializedListener {

	private YouTubePlayerView youTubeView;
	private TextView contentTv;
	private String videoId = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_layout);

		String text = null;

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(KeyUtils.VIDEO_ID_KEY))
			videoId = extras.getString(KeyUtils.VIDEO_ID_KEY);
		if (extras.containsKey(KeyUtils.TEXT_KEY))
			text = extras.getString(KeyUtils.TEXT_KEY);

		initViews();
		ViewInitializer.initContentText(this, contentTv, text.trim());

	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {

	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo(videoId);
		}
	}

	private void initViews() {
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(KeyUtils.DEVELOPER_KEY, this);

		contentTv = (TextView) findViewById(R.id.contentTv);
	}
}

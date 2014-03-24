package com.tntu.easyenglish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.view.TranslationDialog;

public class YoutubeActivity extends YouTubeBaseActivity implements
		YouTubePlayer.OnInitializedListener {

	private YouTubePlayerView youTubeView;
	private TextView contentTv;

	private String videoId = null;
	private String apiKey = null;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		String text = null;

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(KeyUtils.TITLE_KEY))
			setTitle(extras.getString(KeyUtils.TITLE_KEY));
		if (extras.containsKey(KeyUtils.VIDEO_ID_KEY))
			videoId = extras.getString(KeyUtils.VIDEO_ID_KEY);
		if (extras.containsKey(KeyUtils.TEXT_KEY))
			text = extras.getString(KeyUtils.TEXT_KEY);
		if (extras.containsKey(KeyUtils.API_KEY))
			apiKey = extras.getString(KeyUtils.API_KEY);

		initViews();
		TranslationDialog dialog = new TranslationDialog(this, apiKey);
		dialog.initContentText(contentTv, text.trim());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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

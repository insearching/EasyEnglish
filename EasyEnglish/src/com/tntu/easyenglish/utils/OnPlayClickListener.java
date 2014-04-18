package com.tntu.easyenglish.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class OnPlayClickListener implements OnClickListener {

	private String soundLink;

	private boolean playPause;
	private MediaPlayer mediaPlayer;
	private boolean intialStage = true;

	public OnPlayClickListener(String soundLink) {
		this.soundLink = soundLink;

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void onClick(View v) {
		if (soundLink == null || soundLink.equals(""))
			return;
		String url = new StringBuilder(KeyUtils.BASE_URL).append(soundLink)
				.toString();
		if (!playPause) {
			if (intialStage)
				new Player().execute(url);
			else {
				if (!mediaPlayer.isPlaying())
					mediaPlayer.start();
			}
			playPause = true;
		} else {
			if (mediaPlayer.isPlaying())
				mediaPlayer.pause();
			playPause = false;
		}
	}

	class Player extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Boolean prepared;
			try {
				mediaPlayer.setDataSource(params[0]);
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						intialStage = true;
						playPause = false;
						mediaPlayer.stop();
						mediaPlayer.reset();
					}
				});
				mediaPlayer.prepare();
				prepared = true;
			} catch (Exception e) {
				prepared = false;
				e.printStackTrace();
			}
			return prepared;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.d("Prepared", "//" + result);
			mediaPlayer.start();

			intialStage = false;
		}
	}
}
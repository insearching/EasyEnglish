package com.tntu.easyenglish.utils;

import java.io.File;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import com.tntu.easyenglish.R;

public class SoundCacher implements DownloadListener{
	private Context mContext;
	private DownloadService mService;

	private boolean playPause;
	private MediaPlayer mediaPlayer;
	private boolean intialStage = true;
	private String filePath = null;

	public SoundCacher(Context context) {
		mContext = context;

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		filePath = Environment.getExternalStorageDirectory().getPath() + "/"
				+ context.getString(R.string.app_name);
	}

	public void play(String audioUrl) {
		filePath += audioUrl;
		File f = new File(filePath);
		if (f.exists() && !f.isDirectory()) {
			mediaPlayer = MediaPlayer.create(mContext, Uri.parse(filePath));
			mediaPlayer.start();
		} else {
			downloadFile(audioUrl, filePath);
		}
	}

	public void downloadFile(String audioUrl, String fileName) {
		ServiceConnection mConn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder binder) {
				mService = ((DownloadService.FileDownloadBinder) binder)
						.getService();
				mService.attachListener(mContext);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
		Intent intent = new Intent(mContext, DownloadService.class);
		intent.putExtra(KeyUtils.AUDIO_KEY, audioUrl);
		intent.putExtra(KeyUtils.FILE_NAME_KEY, fileName);

		mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
		mContext.startService(intent);
	}

	@Override
	public void onDownloadCompleted(String filePath) {
		mediaPlayer = MediaPlayer.create(mContext, Uri.parse(filePath));
		mediaPlayer.start();
	}
}

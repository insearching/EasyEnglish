package com.tntu.easyenglish.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.tntu.easyenglish.R;

public class SoundCacher {
	private Context mContext;
	private MediaPlayer mediaPlayer;
	private String baseLocalPath = null;
	private String url;
	private String filePath;

	public SoundCacher(Context context, String audioUrl) {
		mContext = context;

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		baseLocalPath = Environment.getExternalStorageDirectory().getPath()
				+ "/" + context.getString(R.string.app_name);
		url = KeyUtils.BASE_URL + audioUrl;
		filePath = baseLocalPath + audioUrl;
	}

	public void play() {
		File f = new File(filePath);
		if (f.exists() && !f.isDirectory()) {
			mediaPlayer = MediaPlayer.create(mContext, Uri.parse(filePath));
			if (mediaPlayer != null)
				mediaPlayer.start();
			else {
				Toast.makeText(mContext,
						mContext.getString(R.string.failed_to_load_sound),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			DownloadTask task = new DownloadTask();
			task.execute(url, filePath);
		}
	}

	private class DownloadTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try {
				URL url = new URL(params[0]);
				String filePath = params[1];
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "Server returned HTTP "
							+ connection.getResponseCode() + " "
							+ connection.getResponseMessage();
				}

				// download the file
				input = connection.getInputStream();
				output = new FileOutputStream(createFile(filePath));

				byte data[] = new byte[4096];
				int count;
				while ((count = input.read(data)) != -1) {
					output.write(data, 0, count);
				}
			} catch (Exception e) {
				return e.toString();
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException ignored) {
				}

				if (connection != null)
					connection.disconnect();
			}
			return "OK";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("OK"))
				play();
		}
	}

	private File createFile(String filePath) {
		File file = new File(filePath);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdir();

		file = null;
		try {
			file = new File(filePath);

			if (!file.createNewFile()) {
				file.delete();
				if (!file.createNewFile()) {
					return null;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return file;
	}
}

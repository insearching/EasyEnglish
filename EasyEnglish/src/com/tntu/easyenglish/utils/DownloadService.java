package com.tntu.easyenglish.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {
	// Application info on service
	private static final String TAG = "DOWANLOAD SERVICE";
	private String EXT_STORAGE_PATH;
	private DownloadListener downloadListener;

	IBinder mBinder = new FileDownloadBinder();

	public class FileDownloadBinder extends Binder {
		public DownloadService getService() {
			return DownloadService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		EXT_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();
		android.os.Debug.waitForDebugger();
		Log.d(TAG, "CREATED");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		String requestUrl = intent.getStringExtra(KeyUtils.AUDIO_KEY);
		String fileName = intent.getStringExtra(KeyUtils.FILE_NAME_KEY);

		DownloadData downloadTask = new DownloadData(startId);
		downloadTask.execute(requestUrl, fileName);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "DESTORY");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	class DownloadData extends AsyncTask<String, Integer, Integer> {
		int startId;
		String requestUrl = null;
		String fileName = null;

		public DownloadData(int startId) {
			this.startId = startId;
		}

		@Override
		protected Integer doInBackground(String... param) {
			requestUrl = param[0];
			fileName = param[1];
			Integer count, result = null;
			try {

				URL url = new URL(requestUrl);
				URLConnection conection = url.openConnection();
				conection.connect();
				int lengthOfFile = conection.getContentLength();
				Log.d(TAG, "Length of file: " + lengthOfFile);
				InputStream input = new BufferedInputStream(url.openStream(),
						8192);
				File file = createFile(fileName);
				OutputStream output = new FileOutputStream(file);
				byte data[] = new byte[1024];
				while ((count = input.read(data)) != -1) {
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
				result = 1;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				result = -1;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Log.i(TAG, String.valueOf(result));
			if (result != null && result == 1) {
				if (downloadListener != null)
					downloadListener.onDownloadCompleted(fileName);
			}
			stopSelf(startId);
		}
	}

	private File createFile(String fileName) {
		String sFolder = EXT_STORAGE_PATH + "/";
		File file = new File(sFolder);
		if (!file.exists())
			file.mkdirs();

		file = null;
		try {
			file = new File(sFolder + fileName);

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

	public void attachListener(Context context) {
		downloadListener = (DownloadListener) context;
	}
}

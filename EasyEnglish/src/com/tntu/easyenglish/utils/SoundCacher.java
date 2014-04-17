package com.tntu.easyenglish.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class SoundCacher {
	Context mContext;
	DownloadService mService;
	
	public SoundCacher(Context context){
		mContext = context;
	}
	
	public void downloadFile(String audioUrl){
		ServiceConnection mConn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder binder) {
				mService = ((DownloadService.FileDownloadBinder) binder).getService();
				mService.attachListener(mContext);
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
		Intent intent = new Intent(mContext, DownloadService.class)
				.putExtra(KeyUtils.AUDIO_KEY, audioUrl);
		mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
		mContext.startService(intent);
	}
}

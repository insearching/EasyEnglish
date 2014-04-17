package com.tntu.easyenglish.utils;

public interface DownloadListener {
	
	public void onDownloadStarted(String name);
	public void onDownloadCompleted(String name);
	
}

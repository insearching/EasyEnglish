package com.tntu.easyenglish.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class GETClient extends AsyncTask<String, Void, String> {

	private GETListener listener;
	private GETListenerMethod methodListner;
	private String method = null;
	
	private String uui;

	public GETClient (GETListener listener){
		this.listener = listener;
	}
	
	public GETClient (GETListenerMethod listener, String method){
		this.methodListner = listener;
		this.method = method;
	}
	
	public GETClient (String str){
		uui = str;
	}
	
	public String doInBg() {
		return doInBackground(uui);
	}
	
	@Override
	protected String doInBackground(String... params) {
		String responseStr  = null;
		try {
			String url = params[0];
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (response != null) {
				responseStr = EntityUtils.toString(entity);
			}

		} catch (Exception e) {
			Log.e("GET", e.getMessage());
		}
		return responseStr;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result == null || result.equals("")){
			result = "null";
		}
		
		if(method == null)
			listener.onRemoteCallComplete(result);
		else
			methodListner.onRemoteCallComplete(result, method);
		
	}
	
	public interface GETListener {
		public void onRemoteCallComplete(String json);
	}
	
	public interface GETListenerMethod {
		public void onRemoteCallComplete(String json, String method);
	}
}

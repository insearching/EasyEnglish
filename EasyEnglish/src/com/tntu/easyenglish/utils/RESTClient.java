package com.tntu.easyenglish.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tntu.easyenglish.R;

public class RESTClient extends AsyncTask<String, Void, String> {

	private JSONCompleteListener listener;
	private JSONCompleteListenerMethod methodListner;
	private String method = null;

	public RESTClient (JSONCompleteListener listener){
		this.listener = listener;
	}
	
	public RESTClient (JSONCompleteListenerMethod listener, String method){
		this.methodListner = listener;
		this.method = method;
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
			Log.e("REST", e.getMessage());
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
	
	public interface JSONCompleteListener {
		public void onRemoteCallComplete(String json);
	}
	
	public interface JSONCompleteListenerMethod {
		public void onRemoteCallComplete(String json, String method);
	}
}

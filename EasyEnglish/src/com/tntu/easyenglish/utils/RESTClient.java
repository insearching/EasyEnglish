package com.tntu.easyenglish.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class RESTClient extends AsyncTask<String, Void, String> {

	private JSONCompleteListener listner;
	private JSONCompleteListenerMethod methodListner;
	private String method = null;

	public RESTClient (JSONCompleteListener listener){
		this.listner = listener;
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
			//Log.e(TAG, e.getMessage());
		}
		return responseStr;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result == null || result.equals(""))
			return;
		
		if(method == null)
			listner.onRemoteCallComplete(result);
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

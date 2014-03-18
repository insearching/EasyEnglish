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

	public RESTClient (JSONCompleteListener context){
		this.listner = context;
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
		
		listner.onRemoteCallComplete(result);

	}
	
	public interface JSONCompleteListener {
		public void onRemoteCallComplete(String json);
	}
}

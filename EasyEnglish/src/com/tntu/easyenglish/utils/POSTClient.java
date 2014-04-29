package com.tntu.easyenglish.utils;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class POSTClient extends AsyncTask<String, Void, String> {

	private POSTListener listener;
	private POSTListenerMethod methodListner;
	private String method = null;
	private ArrayList<NameValuePair> data;


	public POSTClient(POSTListener listener, ArrayList<NameValuePair> data) {
		this.listener = listener;
		this.data = data;
	}

	public POSTClient(POSTListenerMethod listener, String method) {
		this.methodListner = listener;
		this.method = method;
	}

	@Override
	protected String doInBackground(String... params) {
		String responseStr = null;
		try {
			String url = params[0];
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(data));

			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			if (response != null) {
				responseStr = EntityUtils.toString(entity);
			}

		} catch (Exception e) {
			Log.e("POST", e.getMessage());
		}
		return responseStr;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result == null || result.equals("")) {
			result = "null";
		}
		Log.d("POST", result);

		if (method == null)
			listener.onRemoteCallComplete(result);
		else
			methodListner.onRemoteCallComplete(result, method);

	}

	public interface POSTListener {
		public void onRemoteCallComplete(String json);
	}

	public interface POSTListenerMethod {
		public void onRemoteCallComplete(String json, String method);
	}
}

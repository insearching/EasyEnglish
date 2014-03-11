package com.tntu.easyenglish;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);

		new ShowSplashScreenTask().execute(1000);
	}
	
	class ShowSplashScreenTask extends AsyncTask<Integer, Void, Void>{
		
		@Override
		protected Void doInBackground(Integer... params) {
			
			try {
				Thread.sleep(params[0]);
			} catch (Exception e) { 
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			startActivity(new Intent(SplashActivity.this, LoginActivity.class));
			finish();
		}
		
	}

}

package com.tntu.easyenglish;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.view.TranslationDialog;

public class ContentActivity extends ActionBarActivity {

	private TextView contentTv;

	private String title = null;
	private String text = null;
	private String apiKey = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_layout);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		contentTv = (TextView) findViewById(R.id.contentTv);

		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey(KeyUtils.TITLE_KEY)) {
			title = bundle.getString(KeyUtils.TITLE_KEY);
		}
		if (bundle.containsKey(KeyUtils.TEXT_KEY)) {
			text = bundle.getString(KeyUtils.TEXT_KEY);
		}
		if (bundle.containsKey(KeyUtils.API_KEY)) {
			apiKey = bundle.getString(KeyUtils.API_KEY);
		}

		setTitle(title);
		
		TranslationDialog dialog = new TranslationDialog(this, apiKey);
		dialog.initContentText(contentTv, text.trim());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return true;
	}
}

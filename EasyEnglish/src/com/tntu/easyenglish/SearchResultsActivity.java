package com.tntu.easyenglish;

import java.util.ArrayList;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tntu.easyenglish.adapter.TranslationAdatper;
import com.tntu.easyenglish.entity.Translation;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.GETClient;
import com.tntu.easyenglish.utils.GETClient.GETListener;

public class SearchResultsActivity extends ActionBarActivity implements
		GETListener {

	private ListView searchLv;
	private ProgressBar loadPb;

	private String mQuery = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_layout);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(SearchManager.QUERY))
				mQuery = extras.getString(SearchManager.QUERY);
		}

		initViews();
		setData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		default:
			return true;
		}
	}

	private void initViews() {
		searchLv = (ListView) findViewById(R.id.searchLv);
		loadPb = (ProgressBar) findViewById(R.id.loadPb);
	}

	private void setData() {
		if (mQuery == null)
			return;
		hideView();
		GETClient client = new GETClient(this);
		client.execute("http://easy-english.yzi.me/api/translate?api_key=rakivatake&text="
				+ mQuery);
	}

	@Override
	public void onRemoteCallComplete(String json) {
		if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_TRUE)) {
			ArrayList<Translation> data = JSONUtils.getTranslation(json);
			TranslationAdatper adapter = new TranslationAdatper(this, data);
			searchLv.setAdapter(adapter);
		} else {
			Toast.makeText(this, getString(R.string.failed_to_retrieve_data),
					Toast.LENGTH_SHORT).show();
		}

		showView();
	}

	private void showView() {
		loadPb.setVisibility(View.INVISIBLE);
		searchLv.setVisibility(View.VISIBLE);
	}

	private void hideView() {
		loadPb.setVisibility(View.VISIBLE);
		searchLv.setVisibility(View.INVISIBLE);
	}

}

package com.tntu.easyenglish;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class SearchResultsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Toast.makeText(this, query, Toast.LENGTH_LONG).show();
		}
	}
}

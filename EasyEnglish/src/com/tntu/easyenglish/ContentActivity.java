package com.tntu.easyenglish;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

import com.tntu.easyenglish.adapter.ContentListAdapter;
import com.tntu.easyenglish.entity.Content;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;

public class ContentActivity extends Activity {

	private ContentListAdapter mAdapter;
	private String json = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_layout);

		Bundle bundle = getIntent().getExtras();
		if(bundle.containsKey(KeyUtils.JSON_KEY)){
			json = bundle.getString(KeyUtils.JSON_KEY);
		}
		ArrayList<Content> contentList = JSONUtils.getContentList(json);
		mAdapter = new ContentListAdapter(this, contentList);
		//contentLv.setAdapter(mAdapter);
	}

}

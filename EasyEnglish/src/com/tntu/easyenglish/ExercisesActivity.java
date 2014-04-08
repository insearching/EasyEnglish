package com.tntu.easyenglish;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.tntu.easyenglish.entity.WordTrans;
import com.tntu.easyenglish.exercise.ExerciseListener;
import com.tntu.easyenglish.exercise.TransWordFragment;
import com.tntu.easyenglish.exercise.WordTransFragment;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListenerMethod;

public class ExercisesActivity extends ActionBarActivity implements
		JSONCompleteListenerMethod, ExerciseListener {

	private String mApiKey = null;
	private String mType = null;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

	private int counter = 0;
	private JSONArray results;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercises_layout);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mPager = (ViewPager) findViewById(R.id.pager);
		results = new JSONArray();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(KeyUtils.EXERCISES_TYPE_KEY)) {
				mType = extras.getString(KeyUtils.EXERCISES_TYPE_KEY);
			}
			if (extras.containsKey(KeyUtils.API_KEY)) {
				mApiKey = extras.getString(KeyUtils.API_KEY);
			}
		}

		RESTClient client = new RESTClient(this, mType);
		client.execute("http://easy-english.yzi.me/api/getTraining?api_key=" + mApiKey
				+ "&type=" + mType);
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

	private class ExercisesAdapter extends FragmentStatePagerAdapter {
		ArrayList<WordTrans> data;

		public ExercisesAdapter(FragmentManager fm, ArrayList<WordTrans> data) {
			super(fm);
			this.data = data;
		}

		@Override
		public Fragment getItem(int position) {
			if (mType.equals(KeyUtils.WORD_TRANSLATION_KEY))
				return WordTransFragment.newInstance(mApiKey,
						data.get(position));
			else
				return TransWordFragment.newInstance(mApiKey,
						data.get(position));
		}

		@Override
		public int getCount() {
			return data.size();
		}
	}

	@Override
	public void onRemoteCallComplete(String json, String method) {
		if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_TRUE)) {
			ArrayList<WordTrans> exercises = new ArrayList<WordTrans>();
			if (method.equals(KeyUtils.WORD_TRANSLATION_KEY)) {
				exercises = JSONUtils.getWordTransExercises(json);
			}
			else if (method.equals(KeyUtils.TRANSLATION_WORD_KEY)) {
				exercises = JSONUtils.getTransWordExercises(json);
			}
			mPagerAdapter = new ExercisesAdapter(getSupportFragmentManager(),
					exercises);
			mPager.setAdapter(mPagerAdapter);

		}
	}

	@Override
	public void onTestCompleted(Integer exerciseId, boolean isCorrect,
			String type) {
		JSONObject object = new JSONObject();
		try {
			object.put(KeyUtils.ID_KEY, exerciseId);
			object.put(KeyUtils.CORRECT_KEY, isCorrect);
			results.put(object);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (isCorrect)
			counter++;

		if (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
			JSONObject entireObject = new JSONObject();
			try {
				entireObject.put(KeyUtils.RESULTS_KEY, results);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Toast.makeText(this, "Your score is " + counter, Toast.LENGTH_SHORT)
					.show();

			RESTClient client = new RESTClient(this, "postResults");
			client.execute("http://easy-english.yzi.me/api/processResults?api_key="
					+ mApiKey
					+ "&type="
					+ type
					+ "&results="
					+ entireObject.toString());
			finish();
		}
	}
}

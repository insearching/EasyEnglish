package com.tntu.easyenglish;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.tntu.easyenglish.entity.WordTrans;
import com.tntu.easyenglish.exercise.WordTransFragment;
import com.tntu.easyenglish.exercise.WordTransFragment.ExerciseListener;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.KeyUtils.ExerciseType;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class ExercisesActivity extends FragmentActivity implements
		JSONCompleteListener, ExerciseListener {

	private static final int PAGES_NUM = 10;
	private String mApiKey = null;
	private KeyUtils.ExerciseType mType = KeyUtils.ExerciseType.WORD_TRANSLATION;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ArrayList<Integer> results;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercises_layout);

		mPager = (ViewPager) findViewById(R.id.pager);

		results = new ArrayList<Integer>();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(KeyUtils.EXERCISES_TYPE_KEY)) {
				mType = (KeyUtils.ExerciseType) extras
						.getSerializable(KeyUtils.EXERCISES_TYPE_KEY);
			}
			if (extras.containsKey(KeyUtils.API_KEY)) {
				mApiKey = extras.getString(KeyUtils.API_KEY);
			}
		}

		RESTClient client = new RESTClient(this);
		client.execute(getQuery());
	}

	private String getQuery() {
		if (mType == ExerciseType.WORD_TRANSLATION) {
			return "http://easy-english.yzi.me/api/getTraining?api_key="
					+ mApiKey + "&type=" + KeyUtils.WORD_TRANSLATION_KEY;
		}
		if (mType == ExerciseType.TRANSLATION_WORD) {
			return "http://easy-english.yzi.me/api/getTraining?api_key="
					+ mApiKey + "&type=" + KeyUtils.TRANSLATION_WORD_KEY;
		}
		if (mType == ExerciseType.WORD_CONSTRUCTOR) {
			return "http://easy-english.yzi.me/api/getTraining?api_key="
					+ mApiKey + "&type=" + KeyUtils.WORD_CONSTRUCTOR_KEY;
		}
		return "http://easy-english.yzi.me/api/getTraining?api_key=" + mApiKey
				+ "&type=" + KeyUtils.LISTENING_KEY;
	}

	private class ExercisesAdapter extends FragmentStatePagerAdapter {
		ArrayList<WordTrans> data;
		public ExercisesAdapter(FragmentManager fm, ArrayList<WordTrans> data) {
			super(fm);
			this.data = data;
		}

		@Override
		public Fragment getItem(int position) {
			
			return WordTransFragment.newInstance(mApiKey, data.get(position));
		}

		@Override
		public int getCount() {
			return data.size();
		}
	}

	@Override
	public void onRemoteCallComplete(String json) {
		if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_TRUE)) {
			ArrayList<WordTrans> exercises = JSONUtils
					.getWordTransExercises(json);
			mPagerAdapter = new ExercisesAdapter(getSupportFragmentManager(), exercises);
			mPager.setAdapter(mPagerAdapter);
			
		}
	}

	@Override
	public void onTestCompleted(Integer answerId) {
		results.add(answerId);
		Toast.makeText(this, "Test completed " + answerId, Toast.LENGTH_SHORT).show();
	}
}

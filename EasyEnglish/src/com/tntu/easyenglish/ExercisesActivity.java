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
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tntu.easyenglish.entity.SoundToWord;
import com.tntu.easyenglish.entity.WordTrans;
import com.tntu.easyenglish.exercise.ExerciseListener;
import com.tntu.easyenglish.exercise.SoundToWordFragment;
import com.tntu.easyenglish.exercise.WordTransFragment;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class ExercisesActivity extends ActionBarActivity implements
		JSONCompleteListener, ExerciseListener {

	private String mApiKey = null;
	private String mType = null;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ProgressBar loadPb;

	private int counter = 0;
	private JSONArray results;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercises_layout);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mPager = (ViewPager) findViewById(R.id.pager);
		loadPb = (ProgressBar) findViewById(R.id.loadPb);

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

		hideView();
		RESTClient client = new RESTClient(this);
		client.execute("http://easy-english.yzi.me/api/getTraining?api_key="
				+ mApiKey + "&type=" + mType);
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


	@Override
	public void onRemoteCallComplete(String json) {
		if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_TRUE)) {
			showView();

			if (mType.equals(KeyUtils.WORD_TRANSLATION_KEY)
					|| mType.equals(KeyUtils.TRANSLATION_WORD_KEY)) {
				ArrayList<WordTrans> exercises = new ArrayList<WordTrans>();
				if (mType.equals(KeyUtils.WORD_TRANSLATION_KEY)) {
					exercises = JSONUtils.getWordTransExercises(json);
				} else if (mType.equals(KeyUtils.TRANSLATION_WORD_KEY)) {
					exercises = JSONUtils.getTransWordExercises(json);
				}
				
				mPagerAdapter = new WordTransAdapter(
						getSupportFragmentManager(), exercises);
				mPager.setAdapter(mPagerAdapter);
				mPager.setOnTouchListener(new OnSwipeTouchListener() {
					public void onSwipeRight() {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
					}
				});
			}
			else if(mType.equals(KeyUtils.LISTENING_KEY)){
				ArrayList<SoundToWord> exercises = JSONUtils.getSoundToWordExercise(json);
				
				mPagerAdapter = new SoundToWordAdapter(
						getSupportFragmentManager(), exercises);
				mPager.setAdapter(mPagerAdapter);
				mPager.setOnTouchListener(new OnSwipeTouchListener() {
					public void onSwipeRight() {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
					}
				});
			}
		} else {
			Toast.makeText(this, "Failed to retrieve data.", Toast.LENGTH_LONG)
					.show();
			finish();
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
	}

	@Override
	public void onExerciseCompleted(String type) {
		JSONObject entireObject = new JSONObject();
		try {
			entireObject.put(KeyUtils.RESULTS_KEY, results);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "Your score is " + counter, Toast.LENGTH_SHORT)
				.show();

		RESTClient client = new RESTClient(this);
		String results = "http://easy-english.yzi.me/api/processResults?api_key="
		+ mApiKey + "&type=" + type + "&results="
		+ entireObject.toString();
		client.execute(results);
		finish();
	}

	private void hideView() {
		loadPb.setVisibility(View.VISIBLE);
		mPager.setVisibility(View.GONE);
	}

	private void showView() {
		loadPb.setVisibility(View.GONE);
		mPager.setVisibility(View.VISIBLE);
	}
	
	private class WordTransAdapter extends FragmentStatePagerAdapter {
		ArrayList<WordTrans> data;

		public WordTransAdapter(FragmentManager fm, ArrayList<WordTrans> data) {
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

	private class SoundToWordAdapter extends FragmentStatePagerAdapter {
		ArrayList<SoundToWord> data;

		public SoundToWordAdapter(FragmentManager fm,
				ArrayList<SoundToWord> data) {
			super(fm);
			this.data = data;
		}

		@Override
		public Fragment getItem(int position) {
			return SoundToWordFragment.newInstance(mApiKey, data.get(position));
		}

		@Override
		public int getCount() {
			return data.size();
		}
	}


	public class OnSwipeTouchListener implements OnTouchListener {

		@SuppressWarnings("deprecation")
		private final GestureDetector gestureDetector = new GestureDetector(
				new GestureListener());

		public boolean onTouch(final View v, final MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}

		private final class GestureListener extends SimpleOnGestureListener {

			private static final int SWIPE_THRESHOLD = 100;
			private static final int SWIPE_VELOCITY_THRESHOLD = 100;

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				onTouch(e);
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				boolean result = false;
				try {
					float diffY = e2.getY() - e1.getY();
					float diffX = e2.getX() - e1.getX();
					if (Math.abs(diffX) > Math.abs(diffY)) {
						if (Math.abs(diffX) > SWIPE_THRESHOLD
								&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
							if (diffX > 0) {
								onSwipeRight();
							} else {
								onSwipeLeft();
							}
						}
					} else {
						// onTouch(e);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return result;
			}
		}

		public void onTouch(MotionEvent e) {
		}

		public void onSwipeRight() {
			// call this only if your condition was set
		}

		public void onSwipeLeft() {
			// nothing, this means,swipes to left will be ignored
		}

		public void onSwipeTop() {
		}

		public void onSwipeBottom() {
		}
	}
}

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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.tntu.easyenglish.entity.WordTrans;
import com.tntu.easyenglish.exercise.ExerciseListener;
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
	
	private boolean testCompleted = false;

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

	private class ExercisesAdapter extends FragmentStatePagerAdapter {
		ArrayList<WordTrans> data;

		public ExercisesAdapter(FragmentManager fm, ArrayList<WordTrans> data) {
			super(fm);
			this.data = data;
		}

		@Override
		public Fragment getItem(int position) {
			if (mType.equals(KeyUtils.WORD_TRANSLATION_KEY) || 
					mType.equals(KeyUtils.TRANSLATION_WORD_KEY))
				return WordTransFragment.newInstance(mApiKey,
						data.get(position));
			else
				return WordTransFragment.newInstance(mApiKey,
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
			if(exercises == null)
				return;
			mPagerAdapter = new ExercisesAdapter(getSupportFragmentManager(),
					exercises);
			mPager.setAdapter(mPagerAdapter);
			
			mPager.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					testCompleted = false;
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {}
			});
			
			mPager.setOnTouchListener(new OnSwipeTouchListener() {
				public void onSwipeRight() {
					mPager.setCurrentItem(mPager.getCurrentItem()+1, true);
				}
				
				public void onSwipeLeft(){
					if(!testCompleted)
						mPager.setCurrentItem(mPager.getCurrentItem()-1, true);
				}
			});
		}
	}

	@Override
	public void onTestCompleted(Integer exerciseId, boolean isCorrect,
			String type) {
		testCompleted = true;
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

			RESTClient client = new RESTClient(this, KeyUtils.POST_RESULTS);
			client.execute("http://easy-english.yzi.me/api/processResults?api_key="
					+ mApiKey
					+ "&type="
					+ type
					+ "&results="
					+ entireObject.toString());
			finish();
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

package com.tntu.easyenglish;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tntu.easyenglish.entity.BuildWord;
import com.tntu.easyenglish.entity.SoundToWord;
import com.tntu.easyenglish.entity.WordTrans;
import com.tntu.easyenglish.exercise.ExerciseListener;
import com.tntu.easyenglish.exercise.SoundToWordFragment;
import com.tntu.easyenglish.exercise.WordConstructorFragment;
import com.tntu.easyenglish.exercise.WordTransFragment;
import com.tntu.easyenglish.utils.GETClient;
import com.tntu.easyenglish.utils.GETClient.GETListener;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.POSTClient;
import com.tntu.easyenglish.utils.POSTClient.POSTListener;

public class ExercisesActivity extends ActionBarActivity implements
		GETListener, POSTListener, ExerciseListener {

	private String mApiKey = null;
	private String mType = null;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ProgressBar loadPb;

	private int trueCounter = 0;
	private JSONArray results;

	private ArrayList<NameValuePair> params;

	private final static int WORD_TRANS_KEY = 0;
	private final static int BUILD_WORD_KEY = 1;
	private final static int SOUND_TO_WORD_KEY = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercises_layout);
		getSupportActionBar().setHomeButtonEnabled(true);

		mPager = (ViewPager) findViewById(R.id.pager);
		loadPb = (ProgressBar) findViewById(R.id.loadPb);

		results = new JSONArray();
		params = new ArrayList<NameValuePair>();

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
		GETClient client = new GETClient(this);
		client.execute("http://easy-english.yzi.me/api/getTraining?api_key="
				+ mApiKey + "&type=" + mType);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exercise_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onExerciseCompleted(mType);
			return true;

		case R.id.menu_next:
			mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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

				mPagerAdapter = new ExerciseFragmentAdapter(
						getSupportFragmentManager(), exercises, WORD_TRANS_KEY);
				mPager.setAdapter(mPagerAdapter);
				mPager.setOnTouchListener(new OnSwipeTouchListener() {
					public void onSwipeRight() {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
					}
				});
			} else if (mType.equals(KeyUtils.BUILD_WORD_KEY)) {
				ArrayList<BuildWord> exercises = JSONUtils
						.getBuildWordExercises(json);

				mPagerAdapter = new BuildWordAdapter(
						getSupportFragmentManager(), exercises);
				mPager.setAdapter(mPagerAdapter);
				mPager.setOnTouchListener(new OnSwipeTouchListener() {
					public void onSwipeRight() {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
					}
				});
			} else if (mType.equals(KeyUtils.SOUND_TO_WORD_KEY)) {
				ArrayList<SoundToWord> exercises = JSONUtils
						.getSoundToWordExercises(json);

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

	private int counter = 0;

	@Override
	public void onTestCompleted(Integer exerciseId, boolean isCorrect,
			String type) {
		
		params.add(new BasicNameValuePair(KeyUtils.RESULTS_KEY + "["
				+ counter + "][" + KeyUtils.ID_KEY + "]", String
				.valueOf(exerciseId)));
		params.add(new BasicNameValuePair(KeyUtils.RESULTS_KEY + "["
				+ counter + "][" + KeyUtils.CORRECT_KEY + "]", String
				.valueOf(isCorrect)));
		
		counter++;
		if (isCorrect)
			trueCounter++;
	}

	@Override
	public void onExerciseCompleted(String type) {
		JSONObject entireObject = new JSONObject();
		try {
			entireObject.put(KeyUtils.RESULTS_KEY, results);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "Your score is " + trueCounter, Toast.LENGTH_SHORT)
				.show();

		params.add(new BasicNameValuePair(KeyUtils.API_KEY, mApiKey));
		params.add(new BasicNameValuePair(KeyUtils.TYPE_KEY, type));

		POSTClient client = new POSTClient(this, params);
		String url = "http://easy-english.yzi.me/api/processResults";
		client.execute(url);
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

	private class ExerciseFragmentAdapter extends FragmentStatePagerAdapter {
		ArrayList data;
		int type;

		public ExerciseFragmentAdapter(FragmentManager fm, ArrayList data,
				int type) {
			super(fm);
			this.data = data;
			this.type = type;
		}

		@Override
		public Fragment getItem(int position) {
			switch (type) {
			case WORD_TRANS_KEY:
				return WordTransFragment.newInstance(mApiKey,
						(WordTrans) data.get(position));

			case BUILD_WORD_KEY:
				return WordConstructorFragment.newInstance(mApiKey,
						(BuildWord) data.get(position));

			case SOUND_TO_WORD_KEY:
				return SoundToWordFragment.newInstance(mApiKey,
						(SoundToWord) data.get(position));
			default:
				return null;
			}

		}

		@Override
		public int getCount() {
			return data.size();
		}
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

	private class BuildWordAdapter extends FragmentStatePagerAdapter {
		ArrayList<BuildWord> data;

		public BuildWordAdapter(FragmentManager fm, ArrayList<BuildWord> data) {
			super(fm);
			this.data = data;
		}

		@Override
		public Fragment getItem(int position) {
			return WordConstructorFragment.newInstance(mApiKey,
					data.get(position));
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

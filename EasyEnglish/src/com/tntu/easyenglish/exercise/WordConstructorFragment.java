package com.tntu.easyenglish.exercise;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.BuildWord;
import com.tntu.easyenglish.utils.ImageLoader;
import com.tntu.easyenglish.utils.KeyUtils;

public class WordConstructorFragment extends Fragment implements
		OnClickListener {

	private ExerciseListener listener;
	private View convertView;
	private ImageView wordIv;
	private ImageView audioIv;
	private TextView dontKnowTv;
	private TextView transTv;
	private BuildWord mExercise;

	private LinearLayout origLl;
	private LinearLayout answerLl;
	private TextView emptyLetterTv;
	private TextView letterTv;

	private boolean playPause;
	private MediaPlayer mediaPlayer;
	private boolean intialStage = true;

	private int currentLetter = 0;
	private boolean result = false;

	private static final String mType = KeyUtils.SOUND_TO_WORD_KEY;

	public static WordConstructorFragment newInstance(String apiKey,
			BuildWord exercise) {
		WordConstructorFragment fragment = new WordConstructorFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		args.putSerializable(KeyUtils.EXERCISE_KEY, exercise);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.listener = (ExerciseListener) activity;
		setHasOptionsMenu(true);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setVolume(1, 1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initView(inflater);

		mExercise = (BuildWord) getArguments().getSerializable(
				KeyUtils.EXERCISE_KEY);

		setData(inflater);

		return convertView;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_finish:
			listener.onExerciseCompleted(mType);
			return true;
		}
		return true;
	}

	private void initView(LayoutInflater inflater) {
		convertView = inflater.inflate(R.layout.constructor_fragment, null,
				false);
		wordIv = (ImageView) convertView.findViewById(R.id.wordIv);
		audioIv = (ImageView) convertView.findViewById(R.id.audioIv);
		transTv = (TextView) convertView.findViewById(R.id.transTv);

		dontKnowTv = (TextView) convertView.findViewById(R.id.dontKnowTv);

		origLl = (LinearLayout) convertView.findViewById(R.id.origLl);
		answerLl = (LinearLayout) convertView.findViewById(R.id.answerLl);

	}

	private void setData(LayoutInflater inflater) {

		audioIv.setOnClickListener(pausePlayListener);

		char[] symbols = mExercise.getSymbols();
		for (int i = 0; i < symbols.length; i++) {
			letterTv = (TextView) inflater.inflate(R.layout.letter_view,
					answerLl, false);
			letterTv.setText("" + symbols[i]);
			letterTv.setOnClickListener(onLetterClickListener);
			answerLl.addView(letterTv);

			emptyLetterTv = (TextView) inflater.inflate(
					R.layout.empty_letter_view, origLl, false);
			origLl.addView(emptyLetterTv);
		}

		transTv.setText(mExercise.getTranslation());
		dontKnowTv.setOnClickListener(this);
	}

	private OnClickListener onLetterClickListener = new OnClickListener() {
		public void onClick(View v) {

			View curEmptyView = origLl.getChildAt(currentLetter);
			String phrase = mExercise.getPhrase();
			char c1 = ((TextView) v).getText().toString().charAt(0);
			char c2 = phrase.charAt(currentLetter);

			if (c1 != c2) {
				for (int i = 0; i < 3; i++) {
					new BlinkBackground(curEmptyView, getResources()
							.getDrawable(R.drawable.wrong_letter_bg)).execute(true);
				}
				return;
			}

			
			answerLl.removeView(v);
			origLl.removeView(curEmptyView);
			origLl.addView(v, currentLetter);

			currentLetter++;
			
			if(c2 == phrase.charAt(phrase.length()-1)){
				result = true;
				ImageLoader loader = new ImageLoader(getActivity(), AnimationUtils.loadAnimation(getActivity(), R.anim.fly_in_anim));
				loader.displayImage(mExercise.getPictureLink(), wordIv, false);
			}
			
			listener.onTestCompleted(mExercise.getId(), result, mType);
		};
	};

	private int blinkCount = 4;
	class BlinkBackground extends AsyncTask<Boolean, Void, Boolean> {
		View v;
		Drawable dr;

		public BlinkBackground(View v, Drawable dr) {
			this.v = v;
			this.dr = dr;
		}

		@Override
		protected Boolean doInBackground(Boolean... params) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return params[0];
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			v.setBackground(dr);

			if(blinkCount > 0) {
				blinkCount--;
				if (result) {
					new BlinkBackground(v, getResources().getDrawable(
							R.drawable.empty_letter_bg)).execute(false);
				} else {
					new BlinkBackground(v, getResources().getDrawable(
							R.drawable.wrong_letter_bg)).execute(true);
				}
			}
			else blinkCount = 3;
		}
	}

	private OnClickListener pausePlayListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String soundLink = mExercise.getVoiceLink();
			if (soundLink == null || soundLink.equals(""))
				return;
			String url = new StringBuilder(KeyUtils.BASE_URL).append(soundLink)
					.toString();
			if (!playPause) {
				if (intialStage)
					new Player().execute(url);
				else {
					if (!mediaPlayer.isPlaying())
						mediaPlayer.start();
				}
				playPause = true;
			} else {
				if (mediaPlayer.isPlaying())
					mediaPlayer.pause();
				playPause = false;
			}
		}
	};

	class Player extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Boolean prepared;
			try {
				mediaPlayer.setDataSource(params[0]);
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						intialStage = true;
						playPause = false;
						mediaPlayer.stop();
						mediaPlayer.reset();
					}
				});
				mediaPlayer.prepare();
				prepared = true;
			} catch (Exception e) {
				prepared = false;

				e.printStackTrace();
			}
			return prepared;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mediaPlayer.start();
			if (!result)
				Toast.makeText(getActivity(),
						"Failed to load audio. Try again later.",
						Toast.LENGTH_SHORT).show();
			intialStage = false;
		}
	}

	@Override
	public void onClick(View v) {
		// String asnwer = answerEt.getText().toString();
		// origTv.setText(mExercise.getPhrase());
		//
		// Animation anim = AnimationUtils.loadAnimation(getActivity(),
		// R.anim.fly_in_anim);
		// ImageLoader loader = new ImageLoader(getActivity(), anim);
		//
		// switch (v.getId()) {
		// case R.id.doneTv:
		//
		// final boolean isCorrect = asnwer.equals(mExercise.getPhrase()) ? true
		// : false;
		//
		// listener.onTestCompleted(mExercise.getId(), isCorrect, mType);
		// loader.displayImage(mExercise.getPictureLink(), wordIv, false);
		//
		// answerEt.setEnabled(false);
		// origTv.setVisibility(View.VISIBLE);
		// break;
		//
		// case R.id.dontKnowTv:
		// origTv.setVisibility(View.VISIBLE);
		// loader.displayImage(mExercise.getPictureLink(), wordIv, false);
		// listener.onTestCompleted(mExercise.getId(), false, mType);
		// break;
		//
		// default:
		// break;
		// }

	}
}

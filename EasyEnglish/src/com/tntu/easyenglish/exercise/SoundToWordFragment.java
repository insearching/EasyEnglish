package com.tntu.easyenglish.exercise;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.SoundToWord;
import com.tntu.easyenglish.utils.KeyUtils;

public class SoundToWordFragment extends Fragment implements OnClickListener {

	private ExerciseListener listener;
	private View convertView;
	private ImageView wordIv;
	private TextView origTv;
	private ImageView audioIv;
	private TextView doneTv;
	private TextView dontKnowTv;
	private TextView finishTv;
	private EditText answerEt;
	private SoundToWord mExercise;

	private boolean playPause;
	private MediaPlayer mediaPlayer;
	private boolean intialStage = true;
	private final static String BASE_URL = "http://easy-english.yzi.me";

	private static final String mType = KeyUtils.LISTENING_KEY;
	private int counter = 0;

	public static SoundToWordFragment newInstance(String apiKey,
			SoundToWord exercise) {
		SoundToWordFragment fragment = new SoundToWordFragment();
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

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setVolume(1, 1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initView(inflater);

		mExercise = (SoundToWord) getArguments().getSerializable(
				KeyUtils.EXERCISE_KEY);
		setData();

		return convertView;

	}

	private void initView(LayoutInflater inflater) {
		convertView = inflater.inflate(R.layout.sound_to_word_fragment, null,
				false);
		wordIv = (ImageView) convertView.findViewById(R.id.wordIv);
		audioIv = (ImageView) convertView.findViewById(R.id.audioIv);
		origTv = (TextView) convertView.findViewById(R.id.origTv);
		answerEt = (EditText) convertView.findViewById(R.id.answerEt);
		
		doneTv = (TextView) convertView.findViewById(R.id.doneTv);
		dontKnowTv = (TextView) convertView.findViewById(R.id.dontKnowTv);
		finishTv = (TextView) convertView.findViewById(R.id.finishTv);
	}

	private void setData() {
		origTv.setText(mExercise.getPhrase());
		audioIv.setOnClickListener(pausePlayListener);
		doneTv.setOnClickListener(this);
		dontKnowTv.setOnClickListener(this);
		finishTv.setOnClickListener(this);
	}

	class UrlImageLoader implements UrlImageViewCallback {
		Animation anim;

		public UrlImageLoader(Animation anim) {
			this.anim = anim;
		}

		@Override
		public void onLoaded(ImageView imageView, Bitmap loadedBitmap,
				String url, boolean loadedFromCache) {
			if (!loadedFromCache) {
				imageView.setVisibility(View.VISIBLE);
				imageView.startAnimation(anim);
			}
		}
	}

	private OnClickListener pausePlayListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String soundLink = mExercise.getVoiceLink();
			if (soundLink == null || soundLink.equals(""))
				return;
			String url = new StringBuilder(BASE_URL).append(soundLink)
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

			intialStage = false;
		}

	}

	@Override
	public void onClick(View v) {
		String asnwer = answerEt.getText().toString();
		switch (v.getId()) {
		case R.id.doneTv:
			origTv.setVisibility(View.VISIBLE);
			if(asnwer.equals(mExercise.getPhrase())){
				counter++;
			}
			break;
			
		case R.id.dontKnowTv:
			origTv.setVisibility(View.VISIBLE);
			break;

		case R.id.finishTv:
			getActivity().finish();
			break;
		default:
			break;
		}

	}
}

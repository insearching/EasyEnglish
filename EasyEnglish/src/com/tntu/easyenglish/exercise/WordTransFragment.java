package com.tntu.easyenglish.exercise;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.AnswersAdapter;
import com.tntu.easyenglish.entity.WordTrans;
import com.tntu.easyenglish.utils.KeyUtils;

public class WordTransFragment extends Fragment implements OnItemClickListener {

	private ExerciseListener listener;
	private View convertView;
	private ImageView wordIv;
	private TextView origTv;
	private TextView contextTv;
	private ListView answersLv;
	private WordTrans mExercise;

	public static WordTransFragment newInstance(String apiKey,
			WordTrans exercise) {
		WordTransFragment fragment = new WordTransFragment();
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initView(inflater);

		mExercise = (WordTrans) getArguments().getSerializable(
				KeyUtils.EXERCISE_KEY);
		setData();

		return convertView;

	}

	private void initView(LayoutInflater inflater) {
		convertView = inflater.inflate(R.layout.word_trans_fragment, null,
				false);
		wordIv = (ImageView) convertView.findViewById(R.id.wordIv);
		origTv = (TextView) convertView.findViewById(R.id.origTv);
		contextTv = (TextView) convertView.findViewById(R.id.contextTv);
		answersLv = (ListView) convertView.findViewById(R.id.answersLv);
	}

	private void setData() {


		origTv.setText(mExercise.getPhrase());
		contextTv.setText(mExercise.getContext());

		HashMap<String, Integer> answers = mExercise.getAnswers();
		AnswersAdapter adapter = new AnswersAdapter(getActivity(), answers);
		answersLv.setAdapter(adapter);
		answersLv.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		UrlImageViewHelper.setUrlDrawable(wordIv, mExercise.getPictureLink(),
				R.drawable.ic_launcher, new UrlImageViewCallback() {
					@Override
					public void onLoaded(ImageView imageView,
							Bitmap loadedBitmap, String url,
							boolean loadedFromCache) {
						if (!loadedFromCache) {
							imageView.setVisibility(View.VISIBLE);
							Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fly_in_anim);
							imageView.startAnimation(anim);
						}
					}
				});
		
		
		int correctAnswerId = mExercise.getCorrectAnswer();
		boolean isCorrect = correctAnswerId == id ? true : false;
		AnswersAdapter adapter = ((AnswersAdapter)answersLv.getAdapter());
		adapter.setCorrectAnswer(adapter.getItemPosition(correctAnswerId));
		if(!isCorrect)
			adapter.setWrongAnswer(position);
		
		((AnswersAdapter)answersLv.getAdapter()).notifyDataSetChanged();
		answersLv.setClickable(false);
		
		listener.onTestCompleted(mExercise.getId(), isCorrect,
				KeyUtils.WORD_TRANSLATION_KEY);
	}
}

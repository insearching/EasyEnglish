package com.tntu.easyenglish.exercise;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.AnswersAdapter;
import com.tntu.easyenglish.entity.WordTrans;
import com.tntu.easyenglish.utils.ImageLoader;
import com.tntu.easyenglish.utils.KeyUtils;

public class WordTransFragment extends Fragment implements OnItemClickListener {

	private ExerciseListener listener;
	private View convertView;
	private ImageView wordIv;
	private TextView origTv;
	private TextView contextTv;
	private ListView answersLv;
	private TextView dontKnowTv;
	private WordTrans mExercise;

	private static final String mType = KeyUtils.WORD_TRANSLATION_KEY;

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
		setHasOptionsMenu(true);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_finish:
			listener.onExerciseCompleted(mType);
			return true;

		case R.id.menu_next:
			// listener.onTestCompleted(exerciseId, isCorrect, type)
			return true;
		}
		return true;
	}

	private void initView(LayoutInflater inflater) {
		convertView = inflater.inflate(R.layout.word_trans_fragment, null,
				false);
		wordIv = (ImageView) convertView.findViewById(R.id.wordIv);
		origTv = (TextView) convertView.findViewById(R.id.origTv);
		contextTv = (TextView) convertView.findViewById(R.id.contextTv);
		answersLv = (ListView) convertView.findViewById(R.id.answersLv);
		dontKnowTv = ((TextView) convertView.findViewById(R.id.dontKnowTv));
		dontKnowTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onTestCompleted(mExercise.getId(), false, mType);

				AnswersAdapter adapter = ((AnswersAdapter) answersLv
						.getAdapter());
				int correctAnswerId = mExercise.getCorrectAnswer();
				showAnswers(correctAnswerId, true,
						adapter.getItemPosition(correctAnswerId));
			}
		});
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
			final long id) {
		dontKnowTv.setClickable(false);
		answersLv.setOnItemClickListener(null);

		int correctAnswerId = mExercise.getCorrectAnswer();
		final boolean isCorrect = correctAnswerId == id ? true : false;

		answersLv.setFocusable(false);

		String url = mExercise.getPictureLink();
		if (url == null || url == "") {
			listener.onTestCompleted(mExercise.getId(), isCorrect, mType);
			return;
		}

		Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.fly_in_anim);
		ImageLoader loader = new ImageLoader(getActivity(), anim);
		loader.displayImage(mExercise.getPictureLink(), wordIv, false);
		listener.onTestCompleted(mExercise.getId(), isCorrect, mType);

		showAnswers(correctAnswerId, isCorrect, position);
	}

	private void showAnswers(int correctAnswerId, boolean isCorrect,
			int position) {
		AnswersAdapter adapter = ((AnswersAdapter) answersLv.getAdapter());
		adapter.setCorrectAnswer(adapter.getItemPosition(correctAnswerId));
		if (!isCorrect)
			adapter.setWrongAnswer(position);

		adapter.notifyDataSetChanged();
	}
}

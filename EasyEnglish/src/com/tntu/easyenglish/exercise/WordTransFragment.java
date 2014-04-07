package com.tntu.easyenglish.exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.WordTrans;
import com.tntu.easyenglish.entity.WordTrans.Answer;
import com.tntu.easyenglish.utils.KeyUtils;

public class WordTransFragment extends Fragment implements OnItemClickListener{

	private ExerciseListener listener;
	private View convertView;
	private ImageView wordIv;
	private TextView origTv;
	private TextView contextTv;
	private ListView answersLv;
	private ArrayAdapter<String> adapter;
	private Map<String, Integer> data;

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
		listener = (ExerciseListener)activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initView(inflater);
		data = new HashMap<String, Integer>();
		
		WordTrans exercise = (WordTrans) getArguments().getSerializable(
				KeyUtils.EXERCISE_KEY);
		setData(exercise);

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

	private void setData(WordTrans exercise) {
		UrlImageViewHelper.setUrlDrawable(wordIv, exercise.getPictureLink(),
				R.drawable.ic_launcher, new UrlImageViewCallback() {
					@Override
					public void onLoaded(ImageView imageView,
							Bitmap loadedBitmap, String url,
							boolean loadedFromCache) {

						if (!loadedFromCache) {
							ScaleAnimation scale = new ScaleAnimation(0, 1, 0,
									1, ScaleAnimation.RELATIVE_TO_SELF, .5f,
									ScaleAnimation.RELATIVE_TO_SELF, .5f);
							scale.setDuration(500);
							scale.setInterpolator(new OvershootInterpolator());
							imageView.startAnimation(scale);
						}
					}
				});

		origTv.setText(exercise.getPhrase());
		contextTv.setText(exercise.getContext());
		Answer[] answers = exercise.getAnswers();
		for(Answer answer: answers){
			data.put(answer.phrase, answer.id);
		}
		String[] phrases = new String[answers.length];
		for(int i=0; i< phrases.length; i++){
			phrases[i] = answers[i].phrase;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, phrases);
		answersLv.setAdapter(adapter);
		answersLv.setOnItemClickListener(this);

	}

	class AnswersAdapter extends BaseAdapter {

		private ArrayList<Answer> data;

		public AnswersAdapter(ArrayList<Answer> data) {
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Answer getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return data.get(position).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			return null;
		}

	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		listener.onTestCompleted((int) (long)id);
	}
	
	public interface ExerciseListener {
		public void onTestCompleted(Integer answerId);
	}
}

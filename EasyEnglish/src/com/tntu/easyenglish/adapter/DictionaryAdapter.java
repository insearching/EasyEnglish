package com.tntu.easyenglish.adapter;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.DictionaryWord;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.OnPlayClickListener;
import com.tntu.easyenglish.utils.SoundCacher;

public class DictionaryAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DictionaryWord> data;

	private boolean playPause;
	private MediaPlayer mediaPlayer;
	private boolean intialStage = true;

	public DictionaryAdapter(Context context, ArrayList<DictionaryWord> data) {
		this.data = data;
		this.context = context;

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	public void addItem(DictionaryWord word) {
		data.add(word);
	}

	@Override
	public DictionaryWord getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).getDictionaryId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.dictionary_row, null);

			holder.soundIv = (ImageView) convertView.findViewById(R.id.soundIv);
			holder.wordTv = (TextView) convertView.findViewById(R.id.wordTv);
			holder.dateTv = (TextView) convertView.findViewById(R.id.dateTv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DictionaryWord dicWord = data.get(position);
		
		String word = dicWord.getWord();
		String translation = dicWord.getTranslations()[0];
		String date = dicWord.getDate();
		
		holder.soundIv.setTag(position);


//		holder.soundIv.setOnClickListener(new OnPlayClickListener(data.get(
//				position).getSound()));
		holder.soundIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String soundLink = data.get((Integer) v.getTag()).getSound();
				SoundCacher cacher = new SoundCacher(context);
				cacher.play(soundLink);
			}
		});
		holder.wordTv.setText(word + " � " + translation);
		holder.dateTv.setText(date);

		return convertView;
	}

	class ViewHolder {
		TextView wordTv;
		TextView dateTv;
		ImageView soundIv;
	}
}

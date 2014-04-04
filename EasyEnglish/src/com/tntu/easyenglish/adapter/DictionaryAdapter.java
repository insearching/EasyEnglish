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

public class DictionaryAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DictionaryWord> data;
	
//	 Mediaplayer
	private boolean playPause;
	private MediaPlayer mediaPlayer;
	private boolean intialStage = true;
	private final static String BASE_URL = "http://easy-english.yzi.me/api"; 
	
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
			holder.soundIv.setTag(position);
			holder.soundIv.setOnClickListener(pausePlay);
			
			holder.wordTv = (TextView) convertView.findViewById(R.id.wordTv);
			holder.dateTv = (TextView) convertView.findViewById(R.id.dateTv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DictionaryWord dicWord = data.get(position);
		
		holder.soundIv.setTag(position);
		holder.soundIv.setOnClickListener(pausePlay);
		String word = dicWord.getWord();
		String translation = dicWord.getTranslations()[0];
		String date = dicWord.getDate();

		holder.wordTv.setText(word + " — " + translation);
		holder.dateTv.setText(date);

		return convertView;
	}

	class ViewHolder {
		TextView wordTv;
		TextView dateTv;
		ImageView soundIv;
	}

	private OnClickListener pausePlay = new OnClickListener() {
	@Override
	public void onClick(View v) {
		String sound = data.get((Integer)v.getTag()).getSound();
		if(sound == null || sound.equals(""))
			return;
		String url = new StringBuilder(BASE_URL).append(sound).toString();
		if (!playPause) {
			// btn.setBackgroundResource(R.drawable.button_pause);
			if (intialStage)
				new Player()
						.execute(url);
			else {
				if (!mediaPlayer.isPlaying())
					mediaPlayer.start();
			}
			playPause = true;
		} else {
			// btn.setBackgroundResource(R.drawable.button_play);
			if (mediaPlayer.isPlaying())
				mediaPlayer.pause();
			playPause = false;
		}
	}
};
class Player extends AsyncTask<String, Void, Boolean> {
	private ProgressDialog progress;

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
					// btn.setBackgroundResource(R.drawable.button_play);
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
		if (progress.isShowing()) {
			progress.cancel();
		}
		Log.d("Prepared", "//" + result);
		mediaPlayer.start();

		intialStage = false;
	}

	public Player() {
		progress = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.progress.setMessage("Buffering...");
		this.progress.show();
	}
}
	
}

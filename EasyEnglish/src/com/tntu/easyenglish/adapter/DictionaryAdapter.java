package com.tntu.easyenglish.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.DictionaryWord;
import com.tntu.easyenglish.utils.ImageLoader;

public class DictionaryAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DictionaryWord> data;
	private boolean mBusy = false;
	ImageLoader loader;

	public DictionaryAdapter(Context context, ArrayList<DictionaryWord> data) {
		this.data = data;
		this.context = context;
		loader = new ImageLoader(context);
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
	
	public void setBusy(boolean flag){
		mBusy = flag;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.dictionary_row, null);

			holder.imageIv = (ImageView) convertView.findViewById(R.id.imageIv);
			holder.wordTv = (TextView) convertView.findViewById(R.id.wordTv);
			holder.transTv = (TextView) convertView.findViewById(R.id.transTv);
			holder.dateTv = (TextView) convertView.findViewById(R.id.dateTv);			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		DictionaryWord dicWord = data.get(position);

		String word = dicWord.getWord();
		String translation = dicWord.getTranslations()[0];
		String date = dicWord.getDate();
		String url = null;
		if (dicWord.getImages() != null && !mBusy) {
			url = dicWord.getImages()[0];

			
			loader.displayImage(url, holder.imageIv, false);
		}

		holder.wordTv.setText(word);
		holder.transTv.setText(translation);
		holder.dateTv.setText(date);
		
		return convertView;
	}

	class ViewHolder {
		TextView wordTv;
		TextView transTv;
		TextView dateTv;
		ImageView imageIv;
	}
}

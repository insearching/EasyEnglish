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
import com.tntu.easyenglish.utils.Word;

public class EntireWordsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Word> data;
	public EntireWordsAdapter(Context context, ArrayList<Word> data){
		this.data = data;
		this.context = context;
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Word getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.word_list_item, null);
			holder.statusIv = (ImageView) convertView.findViewById(R.id.statusIv);
			holder.wordTv = (TextView) convertView.findViewById(R.id.wordTv);
			holder.addWordIv = (ImageView) convertView.findViewById(R.id.addWordIv);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.wordTv.setText(data.get(position).getWord() + " — " +  data.get(position).getTranslation());
		return convertView;
	}
	
	class ViewHolder{
		ImageView statusIv;
		TextView wordTv;
		ImageView addWordIv;
	}

}

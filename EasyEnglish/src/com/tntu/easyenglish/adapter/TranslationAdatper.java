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
import com.tntu.easyenglish.entity.Translation;
import com.tntu.easyenglish.utils.ImageLoader;

public class TranslationAdatper extends BaseAdapter{

	ArrayList<Translation> data;
	Context context;
	public TranslationAdatper(Context context, ArrayList<Translation> data){
		this.data = data;
		this.context = context;
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Translation getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.translation_row, null);
			holder.textTv = (TextView) convertView.findViewById(R.id.textTv);
			holder.imageIv = (ImageView) convertView.findViewById(R.id.imageIv);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		Translation trans = data.get(position);
		ImageLoader loader = new ImageLoader(context);
		loader.displayImage(trans.getImageUrl(), holder.imageIv, false);
		holder.textTv.setText(trans.getText());
		
		return convertView;
	}	
}

class ViewHolder{
	TextView textTv;
	ImageView imageIv;
}
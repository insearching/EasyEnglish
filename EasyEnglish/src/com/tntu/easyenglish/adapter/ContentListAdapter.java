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
import com.tntu.easyenglish.entity.Content;

public class ContentListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Content> data;
	public ContentListAdapter(Context context, ArrayList<Content> data){
		this.data = data;
		this.context = context;
	}
	@Override
	public int getCount() {
		return data.size();
	}

	public void addItem(Content content){
		data.add(content);
	}
	
	@Override
	public Content getItem(int position) {
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
			convertView = inflater.inflate(R.layout.content_list_row, null);
			holder.titleTv = (TextView) convertView.findViewById(R.id.titleTv);
			holder.descrTv = (TextView) convertView.findViewById(R.id.descrTv);
			holder.lvlTv = (ImageView) convertView.findViewById(R.id.lvlIv);
			holder.dateTv = (TextView) convertView.findViewById(R.id.dateTv);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		Content content = data.get(position);
		
		int level = content.getLevel();
		int levelIconRes = R.drawable.level1;
		switch (level) {
		case 1:
			levelIconRes = R.drawable.level1;
			break;

		case 2:
			levelIconRes = R.drawable.level2;
			break;
			
		case 3:
			levelIconRes = R.drawable.level3;
			break;
		}
		holder.titleTv.setText(content.getTitle());
		holder.descrTv.setText(content.getGenre() + " " + content.getType());
		holder.lvlTv.setImageResource(levelIconRes);
		holder.dateTv.setText(content.getDate());
		
		return convertView;
	}
	
	class ViewHolder{
		TextView titleTv;
		TextView descrTv;
		ImageView lvlTv;
		TextView dateTv;
	}
}

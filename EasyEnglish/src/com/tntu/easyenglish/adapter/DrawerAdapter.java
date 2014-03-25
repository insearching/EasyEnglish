package com.tntu.easyenglish.adapter;

import java.util.ArrayList;

import com.tntu.easyenglish.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DrawerAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> data;
	
	public DrawerAdapter(Context context, ArrayList<String> data){
		this.context = context;
		this.data = data;
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public String getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.drawer_row, null);
			holder = new ViewHolder();
			holder.labelTv = (TextView) convertView.findViewById(R.id.labelTv);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.labelTv.setText(data.get(position));
		return convertView;
	}
	
	public void setList(ArrayList<String> data){
		this.data = data;
		notifyDataSetChanged();
	}
	
	class ViewHolder {
		public TextView labelTv;
	}

}

package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.tntu.easyenglish.MainActivity;
import com.tntu.easyenglish.R;

public class MainFragment extends Fragment {
	private View convertView;
	private Bundle mArgs;
	private TextView nameTv;
	private ProfilePictureView profilePicture;
	private Button logintButt;

	public static MainFragment newInstance(Bundle b) {
		MainFragment f = new MainFragment();
		f.setArguments(b);
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState = getArguments();
		super.onSaveInstanceState(outState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initView(inflater);
		
		Bundle args = savedInstanceState;
		if(args == null){
			args = getArguments();
		}
		
		if(args != null)
			if (args.containsKey(MainActivity.NAME_KEY) && args.containsKey(MainActivity.ID_KEY)){
				logintButt.setText(getString(R.string.com_facebook_loginview_log_out_button));
				nameTv.setText("Hello " + args.getString(MainActivity.NAME_KEY) + "!");
				profilePicture.setProfileId(args.getString(MainActivity.ID_KEY));
		}
		
		return convertView;
	}
	
	private void initView(LayoutInflater inflater){
		convertView = inflater.inflate(R.layout.main_fragment, null);
		nameTv = (TextView) convertView.findViewById(R.id.greetTv);
		profilePicture = (ProfilePictureView) convertView.findViewById(R.id.profilePicture);
		logintButt = (Button) convertView.findViewById(R.id.logintButt);
	}
}

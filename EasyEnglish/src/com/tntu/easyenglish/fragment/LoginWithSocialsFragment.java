package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tntu.easyenglish.LoginActivity;
import com.tntu.easyenglish.R;

public class LoginWithSocialsFragment extends Fragment {

	private View convertView;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.login_with_socials_fragment, null);
		initViews();

		return convertView;
	}

	private void initViews() {
		((ImageView)convertView.findViewById(R.id.facebookIv))
				.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((LoginActivity)getActivity()).onFacebookLogin();
			}
		});
	}
}

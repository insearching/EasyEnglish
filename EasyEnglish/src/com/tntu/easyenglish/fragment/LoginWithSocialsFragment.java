package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.tntu.easyenglish.LoginActivity;
import com.tntu.easyenglish.R;

public class LoginWithSocialsFragment extends Fragment {

	private View convertView;
	private LoginButton facebookButt;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.login_with_socials_fragment, null);
		initViews();

		return convertView;
	}

	private void initViews() {
		facebookButt = (LoginButton) convertView.findViewById(R.id.facebookButt);
		facebookButt
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						((LoginActivity)getActivity()).setFBUser(user);
					}
				});
	}
}

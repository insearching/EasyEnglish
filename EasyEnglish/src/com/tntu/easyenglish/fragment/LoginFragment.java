package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tntu.easyenglish.LoginActivity;
import com.tntu.easyenglish.R;

public class LoginFragment extends Fragment {

	private View convertView;


	private EditText loginEt;
	private EditText passEt;
	private TextView submitTv;
	private TextView loginWithSocialsTv;
	private TextView notRegisteredTv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.login_fragment, null);
		initViews();

		return convertView;
	}

	private void initViews() {
		loginEt = (EditText) convertView.findViewById(R.id.loginEt);
		passEt = (EditText) convertView.findViewById(R.id.passEt);
		submitTv = (TextView) convertView.findViewById(R.id.submitTv);
		loginWithSocialsTv = (TextView) convertView.findViewById(R.id.loginWithSocialsTv);
		notRegisteredTv = (TextView) convertView.findViewById(R.id.notRegisteredTv);
		
		submitTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getActivity(), "Reqire API to login", Toast.LENGTH_LONG).show();
			}
		});
		
		loginWithSocialsTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((LoginActivity)getActivity()).openLoginWithSocials();
			}
		});
		
		notRegisteredTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((LoginActivity)getActivity()).openSignup();
			}
		});
	}
}

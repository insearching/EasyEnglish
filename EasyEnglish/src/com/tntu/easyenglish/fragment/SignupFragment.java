package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tntu.easyenglish.R;

public class SignupFragment extends Fragment {

	private View convertView;

	private EditText emailEt;
	private EditText loginEt;
	private EditText passEt;
	private TextView submitTv;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.sign_up_fragment, null);
		initViews();

		return convertView;
	}

	private void initViews() {
		emailEt = (EditText) convertView.findViewById(R.id.emailEt);
		loginEt = (EditText) convertView.findViewById(R.id.loginEt);
		passEt = (EditText) convertView.findViewById(R.id.passEt);
		submitTv = (TextView) convertView.findViewById(R.id.submitTv);
	}
}

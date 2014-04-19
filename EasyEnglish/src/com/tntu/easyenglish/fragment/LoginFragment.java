package com.tntu.easyenglish.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.tntu.easyenglish.LoginActivity;
import com.tntu.easyenglish.MainActivity;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class LoginFragment extends Fragment implements JSONCompleteListener {

	private View convertView;

	private EditText loginEt;
	private EditText passEt;
	private TextView submitTv;
	private LoginButton facebookButt;
	private TextView notRegisteredTv;
	private SignInButton googleButt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.login_fragment, null);

		initViews();
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String apiKey = sharedPrefs.getString(KeyUtils.API_KEY, null);
		if(apiKey != null){
			login(apiKey);
		}

		return convertView;
	}

	private void initViews() {
		loginEt = (EditText) convertView.findViewById(R.id.loginEt);
		passEt = (EditText) convertView.findViewById(R.id.passEt);

		submitTv = (TextView) convertView.findViewById(R.id.submitTv);
		submitTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = loginEt.getText().toString();
				String password = passEt.getText().toString();

				if (username.length() != 0 && password.length() != 0) {
					String requestUrl = "http://easy-english.yzi.me/api/getApiKey?login="
							+ username + "&password=" + password;
					RESTClient client = new RESTClient(LoginFragment.this);
					client.execute(requestUrl);
					
					((LoginActivity)getActivity()).onLoginStarted();
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.empty_credentials),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		facebookButt = (LoginButton) convertView
				.findViewById(R.id.facebookButt);
		facebookButt
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						if (user != null) {
							((LoginActivity) getActivity())
									.onFacebookLoged(user);
						}
					}
				});

		googleButt = (SignInButton) convertView.findViewById(R.id.googleButt);
		googleButt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((LoginActivity) getActivity()).onGoogleLogin();
			}
		});

		notRegisteredTv = (TextView) convertView
				.findViewById(R.id.notRegisteredTv);
		notRegisteredTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((LoginActivity) getActivity()).openSignup();
			}
		});
	}

	public void setGooglePlusButtonText(String buttonText) {
		for (int i = 0; i < googleButt.getChildCount(); i++) {
			View v = googleButt.getChildAt(i);
			if (v instanceof TextView) {
				TextView mTextView = (TextView) v;
				mTextView.setTextSize(16);
				mTextView.setText(buttonText);
				return;
			}
		}
	}

	@Override
	public void onRemoteCallComplete(String json) {
		String apiKey = null;
		if(json.equals("null")){
			Toast.makeText(getActivity(), getActivity().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
			((LoginActivity)getActivity()).onLoginCompleted();
			return;
		}
		if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_TRUE)) {
			loginEt.requestFocus();
			loginEt.setText("");
			passEt.setText("");

			apiKey = JSONUtils.getValueFromData(json, KeyUtils.API_KEY);
			if(apiKey == null || apiKey.equals("null")){
				Toast.makeText(getActivity(), getActivity().getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
				return;
			}
			
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(KeyUtils.API_KEY, apiKey);
			editor.commit();
			
			login(apiKey);
			
		} else {
			loginEt.requestFocus();
			passEt.setText("");
			Toast.makeText(getActivity(),
					getString(R.string.wrong_credentials), Toast.LENGTH_LONG)
					.show();
		}

		((LoginActivity)getActivity()).onLoginCompleted();
	}
	
	private void login(String apiKey){
		Intent intent = new Intent(getActivity(), MainActivity.class);
		
		Bundle args = new Bundle();
		args.putSerializable(KeyUtils.AUTH_KEY, KeyUtils.AuthType.NATIVE);
		args.putString(KeyUtils.API_KEY, apiKey);
		intent.putExtras(args);
		
		startActivity(intent);
		getActivity().finish();
	}
}
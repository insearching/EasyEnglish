package com.tntu.easyenglish.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tntu.easyenglish.MainActivity;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.GETClient;
import com.tntu.easyenglish.utils.GETClient.GETListener;

public class SignupFragment extends Fragment implements GETListener {

	private View convertView;

	private EditText emailEt;
	private EditText loginEt;
	private EditText passEt;
	private EditText confirmPassEt;
	private TextView submitTv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.sign_up_fragment, null);
		initViews();

		submitTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pass = passEt.getText().toString();
				String confPass = confirmPassEt.getText().toString();

				if (pass.equals(confPass)) {
					String result = isCorrectData();
					if (result == null) {
						GETClient client = new GETClient(SignupFragment.this);
						client.execute("http://easy-english.yzi.me/api/registerUser?login="
								+ loginEt.getText().toString()
								+ "&email="
								+ emailEt.getText().toString()
								+ "&password="
								+ passEt.getText().toString());
					} else {
						Toast.makeText(getActivity(), result, Toast.LENGTH_LONG)
								.show();
					}

				} else {
					Toast.makeText(
							getActivity(),
							getActivity()
									.getString(R.string.passwords_not_same),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		return convertView;
	}

	private void initViews() {
		loginEt = (EditText) convertView.findViewById(R.id.logEt);
		emailEt = (EditText) convertView.findViewById(R.id.emailEt);
		passEt = (EditText) convertView.findViewById(R.id.passwordEt);
		confirmPassEt = (EditText) convertView.findViewById(R.id.confirmPassEt);
		submitTv = (TextView) convertView.findViewById(R.id.addWordTv);
	}

	private String isCorrectData() {
		boolean result = false;
		Pattern pattern = Pattern
				.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher = pattern.matcher(emailEt.getText().toString());
		result = matcher.matches();
		if (!result)
			return "Wrond email, please retype.";

		pattern = Pattern.compile("^[a-z]([a-z\\d\\.\\-]{0,18}[a-z\\d])?$",
				Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(loginEt.getText().toString());
		result = matcher.matches();
		if (!result)
			return "Wrong login, please retype.";

		if (passEt.length() < 5 || passEt.length() > 13)
			return "Password has to be in a range from 5 to 13 symbols";
		if (!passEt.getText().toString()
				.equals(confirmPassEt.getText().toString()))
			return "Paswords are not equals!";

		return null;
	}

	@Override
	public void onRemoteCallComplete(String json) {
		if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_TRUE)) {
			String apiKey = JSONUtils.getValueFromData(json, KeyUtils.API_KEY);
			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.putExtra(KeyUtils.API_KEY, apiKey);
			intent.putExtra(KeyUtils.AUTH_KEY, KeyUtils.AuthType.NATIVE);
			startActivity(intent);
		} else if (JSONUtils.getResponseStatus(json).equals(JSONUtils.SUCCESS_FALSE)) {
			String error = JSONUtils.getValue(json, KeyUtils.MSG_KEY);
			Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
		}
	}
}

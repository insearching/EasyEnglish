package com.tntu.easyenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tntu.easyenglish.R;

public class LoginFragment extends Fragment {

	private View convertView;

	private RelativeLayout loginLayout;
	private RelativeLayout loginSocialsLayout;
	private RelativeLayout signupLayout;

	private EditText loginEt;
	private EditText passEt;
	private TextView submitTv;
	private TextView loginWithSocialsTv;
	private TextView notRegisteredTv;

	private ImageView facebookIv;
	private ImageView googleIv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.login_fragment, null);
		initViews();

		return convertView;
	}

	private void initViews() {
		loginLayout = (RelativeLayout) convertView
				.findViewById(R.id.loginLayout);
		loginSocialsLayout = (RelativeLayout) convertView
				.findViewById(R.id.loginSocialLayout);
		signupLayout = (RelativeLayout) convertView
				.findViewById(R.id.signupLayout);
		

		initLoginViews();
	}

	private void initLoginViews() {
		loginEt = (EditText) loginLayout.findViewById(R.id.loginEt);
		passEt = (EditText) loginLayout.findViewById(R.id.passEt);
		submitTv = (TextView) loginLayout.findViewById(R.id.submitTv);
		loginWithSocialsTv = (TextView) loginLayout.findViewById(R.id.loginWithSocialsTv);
		notRegisteredTv = (TextView) loginLayout.findViewById(R.id.notRegisteredTv);
		
		loginWithSocialsTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAnimation(loginLayout, loginSocialsLayout);
			}
		});
		
		notRegisteredTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAnimation(loginLayout, signupLayout);
			}
		});
	}
	
	private void initSignupViews() {
		loginEt = (EditText) loginLayout.findViewById(R.id.loginEt);
		passEt = (EditText) loginLayout.findViewById(R.id.passEt);
		submitTv = (TextView) loginLayout.findViewById(R.id.submitTv);
	}

	private void startAnimation(final View oldView, View newView) {
		newView.setVisibility(View.VISIBLE);
		Animation floatOutAnim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.float_fade_out_anim);
		Animation floatInAnim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.float_fade_in_anim);

		oldView.setAnimation(floatOutAnim);
		newView.setAnimation(floatInAnim);
		floatOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				oldView.setVisibility(View.GONE);
			}
		});
	}
}

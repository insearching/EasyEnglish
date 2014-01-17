package com.tntu.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.tntu.easyenglish.fragment.LoginFragment;
import com.tntu.easyenglish.fragment.LoginWithSocialsFragment;
import com.tntu.easyenglish.fragment.SignupFragment;

public class LoginActivity extends FragmentActivity {

	private View fragmentContainer;

	public static String backStackTag = "main";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		backStackTag = "login";
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStackImmediate();
		}

		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.float_left_to_right_in_anim,
						R.anim.float_left_to_right_out_anim)
				.replace(R.id.fragmentContainer, new LoginFragment())
				.addToBackStack(backStackTag).commit();

	}

	public void openLoginWithSocials() {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.float_left_to_right_in_anim,
						R.anim.float_left_to_right_out_anim, 
						R.anim.float_right_to_left_in_anim,
						R.anim.float_right_to_left_out_anim)
				.replace(R.id.fragmentContainer, new LoginWithSocialsFragment())
				.addToBackStack(backStackTag)
				.commit();
	}
	
	public void openSignup() {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.float_left_to_right_in_anim,
						R.anim.float_left_to_right_out_anim, 
						R.anim.float_right_to_left_in_anim,
						R.anim.float_right_to_left_out_anim)
				.replace(R.id.fragmentContainer, new SignupFragment())
				.addToBackStack(backStackTag)
				.commit();
	}
	
	public void onFacebookLogin() {
		startActivity(new Intent(this, MainActivity.class));
	}
}

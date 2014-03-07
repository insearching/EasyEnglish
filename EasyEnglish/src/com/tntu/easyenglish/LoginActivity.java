package com.tntu.easyenglish;

import com.tntu.easyenglish.fragment.LoginFragment;
import com.tntu.easyenglish.fragment.SignupFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class LoginActivity extends FragmentActivity {

	private static String backStackTag = "login";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.float_left_to_right_in_anim,
						R.anim.float_left_to_right_out_anim,
						R.anim.float_right_to_left_in_anim,
						R.anim.float_right_to_left_out_anim)
				.replace(R.id.fragmentContainer, new LoginFragment())
				.addToBackStack(backStackTag).commit();
	}

}

package com.tntu.easyenglish;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.tntu.easyenglish.fragment.LoginFragment;

public class MainActivity extends FragmentActivity {

	private View fragmentContainer;
	
	private String backStackTag = "main";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		backStackTag = "login";
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStackImmediate();
		}
		
		LoginFragment loginFragment = new LoginFragment();
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentContainer, loginFragment)
				.addToBackStack(backStackTag)
				.commit();
		//fragmentContainer.setVisibility(View.VISIBLE);
		
	}
	
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
	}
}

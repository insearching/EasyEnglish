package com.tntu.easyenglish;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.tntu.easyenglish.fragment.LoginFragment;
import com.tntu.easyenglish.fragment.SignupFragment;

public class LoginActivity extends FragmentActivity {

	private static final String TAG = "EasyEnglish";
	public static String backStackTag = "main";

	private View fragmentContainer;
	private ProgressBar loadPb;
	// Facebook authorization
	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if(state == SessionState.OPENING)
				setLoginVisible(false);
			onSessionStateChanged(session, state, exception);
			Log.d(TAG, state.toString());
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d("EasyEnglish", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("EasyEnglish", "Success!");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.login_activity);

		fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
		loadPb = (ProgressBar) findViewById(R.id.loadPb);
		loadPb.setVisibility(View.INVISIBLE);
		setLoginVisible(true);

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

	public void openSignup() {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.float_left_to_right_in_anim,
						R.anim.float_left_to_right_out_anim,
						R.anim.float_right_to_left_in_anim,
						R.anim.float_right_to_left_out_anim)
				.replace(R.id.fragmentContainer, new SignupFragment())
				.addToBackStack(backStackTag).commit();
	}

	private void onSessionStateChanged(Session session, SessionState state,
			Exception exception) {
		if (exception instanceof FacebookOperationCanceledException
				|| exception instanceof FacebookAuthorizationException) {
			new AlertDialog.Builder(LoginActivity.this)
					.setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.ok, null).show();
		}
	}

//	public void setFBUser(GraphUser user) {
//		Intent i = new Intent(getApplicationContext(), MainActivity.class);
//		i.putExtra("name", user.getName());
//		i.putExtra("id", user.getId());
//		startActivity(i);
//
//		
//	}

	public void setLoginVisible(boolean flag) {
		if (flag) {
			fragmentContainer.setVisibility(View.VISIBLE);
			loadPb.setVisibility(View.GONE);
		} else {
			fragmentContainer.setVisibility(View.GONE);
			loadPb.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

		AppEventsLogger.activateApp(this);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
}

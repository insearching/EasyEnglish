package com.tntu.easyenglish;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.tntu.easyenglish.fragment.LoginFragment;
import com.tntu.easyenglish.fragment.SignupFragment;

public class LoginActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	// Google+
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private SignInButton googleButt;

	private static final String TAG = "EasyEnglish";
	private static String backStackTag = "main";
	private static final String fragment_tag = "login_fragment";

	private View fragmentContainer;
	private ProgressBar loadPb;
	
	// Facebook authorization
	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state == SessionState.OPENING)
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
		setLoginVisible(true);

		mPlusClient = new PlusClient.Builder(this, this, this)
				.setActions("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity")
				.setScopes(Scopes.PLUS_LOGIN) // recommended login scope for
												// social features
				// .setScopes("profile") // alternative basic login scope
				.build();
		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");

		backStackTag = "login";
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStackImmediate();
		}

		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.float_left_to_right_in_anim,
						R.anim.float_left_to_right_out_anim)
				.replace(R.id.fragmentContainer, new LoginFragment(), fragment_tag)
				.addToBackStack(backStackTag).commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
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

	public void onGoogleLogin() {
		if (!mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				try {
					mConnectionResult.startResolutionForResult(
							LoginActivity.this, REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					// Try connecting again.
					mConnectionResult = null;
					mPlusClient.connect();
				}
			}
		} else {

			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();
			LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(fragment_tag);
			fragment.setGooglePlusButtonText(getString(R.string.sign_in));
			Toast.makeText(this, "User is disconnected!", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void setLoginVisible(boolean flag) {
		if (flag) {
			fragmentContainer.setVisibility(View.VISIBLE);
			loadPb.setVisibility(View.GONE);
		} else {
			fragmentContainer.setVisibility(View.GONE);
			loadPb.setVisibility(View.VISIBLE);
		}
	}

	public void setGooglePlusButtonText(SignInButton signInButton,
			String buttonText) {
		for (int i = 0; i < signInButton.getChildCount(); i++) {
			View v = signInButton.getChildAt(i);
			if (v instanceof TextView) {
				TextView mTextView = (TextView) v;
				mTextView.setTextSize(16);
				mTextView.setText(buttonText);
				return;
			}
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
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	// Google+ authorization
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}

		// Save the intent so that we can start an activity when the user clicks
		// the sign-in button.
		mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle arg0) {
		// We've resolved any connection errors.
		mConnectionProgressDialog.dismiss();
		LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(fragment_tag);
		fragment.setGooglePlusButtonText(getString(R.string.sign_out));
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDisconnected() {
	}
}

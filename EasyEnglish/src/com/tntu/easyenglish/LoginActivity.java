package com.tntu.easyenglish;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.tntu.easyenglish.fragment.LoginFragment;
import com.tntu.easyenglish.fragment.SignupFragment;
import com.tntu.easyenglish.utils.KeyUtils;

public class LoginActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	public static final String TAG = "EasyEnglish";

	private static String backStackTag = "login";

	// Login Google+
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private UiLifecycleHelper uiHelper;
	private LoginFragment mFragment;
	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private Bundle mArgs;
	private String mApiKey = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		mFragment = new LoginFragment();
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KeyUtils.USER_KEY))
				mArgs = savedInstanceState.getBundle(KeyUtils.USER_KEY);
			if (savedInstanceState.containsKey(KeyUtils.API_KEY))
				mApiKey = savedInstanceState.getString(KeyUtils.API_KEY);
		}

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		mPlusClient = new PlusClient.Builder(this, this, this)
				.setActions("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity")
				.setScopes(Scopes.PLUS_LOGIN).build();

		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");

		mArgs = new Bundle();

		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.float_left_to_right_in_anim,
						R.anim.float_left_to_right_out_anim,
						R.anim.float_right_to_left_in_anim,
						R.anim.float_right_to_left_out_anim)
				.replace(R.id.fragmentContainer, mFragment)
				.addToBackStack(backStackTag).commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
		outState.putBundle(KeyUtils.USER_KEY, mArgs);
		outState.putString(KeyUtils.API_KEY, mApiKey);
	}

	private void onSessionStateChanged(Session session, SessionState state,
			Exception exception) {
		if (exception instanceof FacebookOperationCanceledException
				|| exception instanceof FacebookAuthorizationException) {
			new AlertDialog.Builder(this).setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(android.R.string.ok, null).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppEventsLogger.activateApp(this);
		uiHelper.onResume();
		mPlusClient.connect();
		
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		mPlusClient.disconnect();
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

	public void onFacebookLoged(GraphUser user) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(KeyUtils.NAME_KEY, user.getName());
		intent.putExtra(KeyUtils.ID_KEY, user.getId());
		intent.putExtra(KeyUtils.AUTH_KEY, KeyUtils.AuthType.FACEBOOK);
		startActivity(intent);
		finish();
	}

	public void onGoogleLogin() {
		if (!mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				try {
					mConnectionResult.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mConnectionResult = null;
					mPlusClient.connect();
				}
			}
		} else {
			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();

			mFragment.setGooglePlusButtonText(getString(R.string.sign_in));
			Toast.makeText(this, "User is disconnected!", Toast.LENGTH_LONG)
					.show();
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

	// Google+ authorization
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
		mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle arg0) {
		mConnectionProgressDialog.dismiss();
		Toast.makeText(this, "Loged in Google+", Toast.LENGTH_LONG).show();
		if (mPlusClient.getCurrentPerson() != null) {
			Person currentPerson = mPlusClient.getCurrentPerson();

			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(KeyUtils.NAME_KEY, currentPerson.getDisplayName());
			intent.putExtra(KeyUtils.ID_KEY, currentPerson.getId());
			intent.putExtra(KeyUtils.AUTH_KEY, KeyUtils.AuthType.GOOGLE);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onDisconnected() {
	}

	// Facebook
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChanged(session, state, exception);
			Log.d(TAG, state.toString());
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d(TAG, String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d(TAG, "Success!");
		}
	};

	public void onLoginStarted() {
		mFragment.getView().setVisibility(View.GONE);
		findViewById(R.id.loginPb).setVisibility(View.VISIBLE);
	}

	public void onLoginCompleted() {
		findViewById(R.id.loginPb).setVisibility(View.GONE);
		mFragment.getView().setVisibility(View.VISIBLE);
	}
}

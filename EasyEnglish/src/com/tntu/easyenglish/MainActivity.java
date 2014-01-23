package com.tntu.easyenglish;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.google.android.gms.plus.model.people.Person.Image;
import com.tntu.easyenglish.adapter.DrawerAdapter;
import com.tntu.easyenglish.fragment.AboutFragment;
import com.tntu.easyenglish.fragment.ContentFragment;
import com.tntu.easyenglish.fragment.DictionaryFragment;
import com.tntu.easyenglish.fragment.ExercisesFragment;
import com.tntu.easyenglish.fragment.LoginFragment;
import com.tntu.easyenglish.fragment.MainFragment;
import com.tntu.easyenglish.fragment.SignupFragment;
import com.tntu.easyenglish.fragment.WordsFragment;

public class MainActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	public static final String USER_KEY = "user";
	public static final String NAME_KEY = "name";
	public static final String ID_KEY = "id";
	public static final String POSITION_KEY = "position";

	private boolean isFirstTimeLaunched;

	private static final int EXERCISES = 0;
	private static final int WORDS = 1;
	private static final int DICTIONARY = 2;
	private static final int CONTENT = 3;
	private static final int ABOUT = 4;
	private static final int SIGN = 5;
	private static final int PROFILE = 6;

	private static String backStackTag = "main";

	private int mPosition = EXERCISES;
	private DrawerAdapter mAdapter;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private Bundle mArgs;

	// Login
	// Google+
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	private static final String TAG = "EasyEnglish";
	private static final String fragment_tag = "login_fragment";

	// Facebook authorization
	private UiLifecycleHelper uiHelper;

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
			Log.d("EasyEnglish", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("EasyEnglish", "Success!");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		mPlusClient = new PlusClient.Builder(this, this, this)
				.setActions("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity")
				.setScopes(Scopes.PLUS_LOGIN).build();

		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
		mArgs = new Bundle();

		initView();

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(POSITION_KEY))
				mPosition = savedInstanceState.getInt(POSITION_KEY);
			if (savedInstanceState.containsKey(USER_KEY))
				mArgs = savedInstanceState.getBundle(USER_KEY);
			isFirstTimeLaunched = false;
		} else {
			isFirstTimeLaunched = true;
		}
		selectItem(mPosition);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	private void initView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mAdapter = new DrawerAdapter(this, new ArrayList<String>(
				Arrays.asList(getResources().getStringArray(
						R.array.main_menu_non_loged))));
		mDrawerList.setAdapter(mAdapter);
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_name,
				R.string.common_google_play_services_enable_text) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
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
		outState.putBundle(USER_KEY, mArgs);
		outState.putInt(POSITION_KEY, mPosition);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
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
				.replace(R.id.content_frame, new SignupFragment())
				.addToBackStack(backStackTag).commit();
	}

	private void onSessionStateChanged(Session session, SessionState state,
			Exception exception) {
		if (exception instanceof FacebookOperationCanceledException
				|| exception instanceof FacebookAuthorizationException) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public void onFacebookLoged(GraphUser user) {
		mArgs = new Bundle();
		mArgs.putString(MainActivity.NAME_KEY, user.getName());
		mArgs.putString(MainActivity.ID_KEY, user.getId());

		mAdapter.setList(new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.main_menu_loged))));

		selectItem(PROFILE);
	}

	public void onGoogleLogin() {
		if (!mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				try {
					mConnectionResult.startResolutionForResult(
							MainActivity.this, REQUEST_CODE_RESOLVE_ERR);
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
			LoginFragment fragment = (LoginFragment) getSupportFragmentManager()
					.findFragmentByTag(fragment_tag);
			fragment.setGooglePlusButtonText(getString(R.string.sign_in));
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

	public void onSignInOut(View v) {
		Session session = Session.getActiveSession();
		if (session != null)
			session.closeAndClearTokenInformation();
		mAdapter.setList(new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.main_menu_non_loged))));
		mArgs = null;
		selectItem(SIGN);

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
		// LoginFragment fragment = (LoginFragment) getSupportFragmentManager()
		// .findFragmentByTag(fragment_tag);
		// fragment.setGooglePlusButtonText(getString(R.string.sign_out));
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		if (mPlusClient.getCurrentPerson() != null) {
			Person currentPerson = mPlusClient.getCurrentPerson();
			String personName = currentPerson.getDisplayName();
			Image personPhoto = currentPerson.getImage();
			String personGooglePlusProfile = currentPerson.getUrl();
			Log.d(TAG, personName);
			Log.d(TAG, personGooglePlusProfile);
		}
	}

	@Override
	public void onDisconnected() {
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		mPosition = position;
		Fragment f = null;
		switch (position) {
		case EXERCISES:
			f = new ExercisesFragment();
			break;
		case WORDS:
			f = new WordsFragment();
			break;
		case DICTIONARY:
			f = new DictionaryFragment();
			break;
		case CONTENT:
			f = new ContentFragment();
			break;
		case ABOUT:
			f = new AboutFragment();
			break;
		case SIGN:
			Session session = Session.getActiveSession();
			if (session != null)
				session.closeAndClearTokenInformation();
			f = new LoginFragment();
			break;
		case PROFILE:
			f = MainFragment.newInstance(mArgs);
			break;
		default:
			f = MainFragment.newInstance(mArgs);
		}
		if (f instanceof LoginFragment) {
			getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.float_left_to_right_in_anim,
							R.anim.float_left_to_right_out_anim)
					.replace(R.id.content_frame, f, fragment_tag)
					.addToBackStack(backStackTag).commit();
		} else {
			getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.float_left_to_right_in_anim,
							R.anim.float_left_to_right_out_anim)
					.replace(R.id.content_frame, f)
					.addToBackStack(backStackTag).commit();
		}
		mDrawerList.setItemChecked(position, true);
		setTitle(mAdapter.getItem(position));
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}
}

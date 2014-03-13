package com.tntu.easyenglish;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.tntu.easyenglish.adapter.DrawerAdapter;
import com.tntu.easyenglish.fragment.AboutFragment;
import com.tntu.easyenglish.fragment.ContentFragment;
import com.tntu.easyenglish.fragment.DictionaryFragment;
import com.tntu.easyenglish.fragment.ExercisesFragment;
import com.tntu.easyenglish.fragment.ProfileFragment;
import com.tntu.easyenglish.fragment.WordsFragment;

public class MainActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	public static final String POSITION_KEY = "position";

	private static final int PROFILE = 0;
	private static final int EXERCISES = 1;
	private static final int WORDS = 2;
	private static final int DICTIONARY = 3;
	private static final int CONTENT = 4;
	private static final int ABOUT = 5;
	private static final int LOGOUT = 6;

	private static String backStackTag = "main";

	private int mPosition = PROFILE;
	private DrawerAdapter mAdapter;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private Bundle mArgs = null;
	private boolean isLogedOut = false;
	private PlusClient mPlusClient;
	private String mApiKey;

	private ContentFragment contentFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(LoginActivity.API_KEY))
				mApiKey = savedInstanceState.getString(LoginActivity.API_KEY);
			if (savedInstanceState.containsKey(LoginActivity.ARGS_KEY))
				mArgs = savedInstanceState.getBundle(LoginActivity.ARGS_KEY);
		}

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(LoginActivity.API_KEY))
				mApiKey = extras.getString(LoginActivity.API_KEY);
			if (extras.containsKey(LoginActivity.ARGS_KEY))
				mArgs = extras.getBundle(LoginActivity.ARGS_KEY);
			if (extras.containsKey(LoginActivity.AUTH_KEY)) {
				LoginActivity.AuthType enumType = (LoginActivity.AuthType) extras
						.getSerializable(LoginActivity.AUTH_KEY);
				if (enumType == LoginActivity.AuthType.NATIVE)
					contentFragment = ContentFragment.newInstance(mApiKey);
			}
		}

		initView();

		selectItem(mPosition, true);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	private void initView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mAdapter = new DrawerAdapter(this, new ArrayList<String>(
				Arrays.asList(getResources().getStringArray(
						R.array.main_menu_loged))));
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(POSITION_KEY, mPosition);
		outState.putString(LoginActivity.API_KEY, mApiKey);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.search);

		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	protected void onResume() {

		super.onResume();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.search:
			getSupportActionBar().setIcon(R.drawable.ic_action_logo);
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

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position, false);
		}
	}

	private void selectItem(int position, boolean isFirstTime) {
		Fragment fragment = null;
		if (isFirstTime || position != mPosition) {
			switch (position) {
			case PROFILE:
				fragment = ProfileFragment.newInstance(mArgs);
				break;
			case EXERCISES:
				fragment = new ExercisesFragment();
				break;
			case WORDS:
				fragment = new WordsFragment();
				break;
			case DICTIONARY:
				fragment = new DictionaryFragment();
				break;
			case CONTENT:
				// Bundle extras = getIntent().getExtras();
				// if(extras != null){
				// if(extras.containsKey(LoginActivity.AUTH_KEY)){
				// LoginActivity.AuthType enumType =
				// (LoginActivity.AuthType)extras.getSerializable(LoginActivity.AUTH_KEY);
				// if(enumType == LoginActivity.AuthType.NATIVE)
				// fragment = ContentFragment.newInstance(mApiKey);
				// }
				// }
				fragment = contentFragment;
				break;
			case ABOUT:
				fragment = new AboutFragment();
				break;
			case LOGOUT:
				callFacebookLogout();
				callGooglePlusLogout();
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra(LoginActivity.LOGOUT_KEY, true);
				startActivity(intent);
				finish();
				return;

			default:
				fragment = ProfileFragment.newInstance(mArgs);
			}

			getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.float_left_to_right_in_anim,
							R.anim.float_left_to_right_out_anim)
					.replace(R.id.content_frame, fragment)
					.addToBackStack(backStackTag).commit();
		}
		mDrawerList.setItemChecked(position, true);
		setTitle(mAdapter.getItem(position));
		mDrawerLayout.closeDrawer(mDrawerList);
		mPosition = position;
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * Logout From Facebook
	 */
	private void callFacebookLogout() {
		Session session = Session.getActiveSession();
		if (session != null) {

			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
			}
		} else {
			session = new Session(this);
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
		}
	}

	private void callGooglePlusLogout() {
		mPlusClient = new PlusClient.Builder(this, this, this)
				.setActions("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity")
				.setScopes(Scopes.PLUS_LOGIN).build();

		if (!mPlusClient.isConnected())
			mPlusClient.connect();
		else {
			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public void onConnected(Bundle arg0) {
		if (!isLogedOut) {
			mPlusClient.clearDefaultAccount();
			mPlusClient.disconnect();
			mPlusClient.connect();
			isLogedOut = true;
		}
	}

	@Override
	public void onDisconnected() {
	}
}

package com.tntu.easyenglish.test;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.tntu.easyenglish.MainActivity;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.DrawerAdapter;
import com.tntu.easyenglish.utils.KeyUtils;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private static final int ADAPTER_COUNT = 7;

	MainActivity mActivity;
	DrawerAdapter mAdapter;
	ActionBarDrawerToggle mDrawerToggle;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		mActivity = getActivity();

		mAdapter = new DrawerAdapter(mActivity, new ArrayList<String>(
				Arrays.asList(mActivity.getResources().getStringArray(
						R.array.main_menu_loged))));

	}
	
	

	public void testDrawerCapacity() {
		assertNotNull(mAdapter);
		assertEquals(ADAPTER_COUNT, mAdapter.getCount());
	}

	public void testDrawerOpened() {
		final View drawer = mActivity.findViewById(android.R.id.home);
		assertNotNull(drawer);

		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				drawer.requestFocus();
				drawer.performClick();
			}
		});
	}

	public void testSearchButton() {
		final View view = mActivity.findViewById(R.id.search);
		assertNotNull(view);

		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				view.requestFocus();
				view.performClick();
			}
		});
	}
}

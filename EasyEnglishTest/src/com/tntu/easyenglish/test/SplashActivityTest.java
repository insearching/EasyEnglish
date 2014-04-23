package com.tntu.easyenglish.test;

import android.test.ActivityInstrumentationTestCase2;
import android.app.Activity;
import junit.framework.AssertionFailedError;
import com.bitbar.recorder.extensions.ExtSolo;
import android.widget.EditText;
import android.widget.TextView;
import java.util.regex.Pattern;

public class SplashActivityTest extends
		ActivityInstrumentationTestCase2<Activity> {

	private static final String LAUNCHER_ACTIVITY_CLASSNAME = "com.tntu.easyenglish.SplashActivity";
	private static Class<?> launchActivityClass;
	static {
		try {
			launchActivityClass = Class.forName(LAUNCHER_ACTIVITY_CLASSNAME);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	private ExtSolo solo; // ExtSolo is an extension of Robotium Solo that helps collecting better test execution data during test runs

	@SuppressWarnings("unchecked")
	public SplashActivityTest() {
		super((Class<Activity>) launchActivityClass);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new ExtSolo(getInstrumentation(), getActivity(), this.getClass()
				.getCanonicalName(), getName());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		solo.tearDown();
		super.tearDown();
	}

	public void testLoginScreen() throws Exception {
		try {
			solo.waitForActivity("SplashActivity");
			solo.waitForActivity("LoginActivity");
			solo.sleep(2000);
			assertTrue(
					"Wait for edit text (id: com.tntu.easyenglish.R.id.loginEt) failed.",
					solo.waitForEditTextById(
							"com.tntu.easyenglish.R.id.loginEt", 2000));
			solo.enterText((EditText) solo
					.findViewById("com.tntu.easyenglish.R.id.loginEt"),
					"insearching");
			
			assertTrue(
					"Wait for edit text (id: com.tntu.easyenglish.R.id.passEt) failed.",
					solo.waitForEditTextById(
							"com.tntu.easyenglish.R.id.passEt", 2000));
			solo.enterText((EditText) solo
					.findViewById("com.tntu.easyenglish.R.id.passEt"),
					"1305920");
			assertTrue(
					"Wait for text (id: com.tntu.easyenglish.R.id.submitTv) failed.",
					solo.waitForTextById("com.tntu.easyenglish.R.id.submitTv",
							2000));
			solo.clickOnText((TextView) solo
					.findViewById("com.tntu.easyenglish.R.id.submitTv"));
			solo.sleep(2000);
			
			assertTrue(solo.searchText(Pattern.quote("insearching")));
			
			solo.sleep(2000);
			
			assertTrue(
					"Wait for edit text (id: com.tntu.easyenglish.R.id.loginEt) failed.",
					solo.waitForEditTextById(
							"com.tntu.easyenglish.R.id.loginEt", 2000));
			solo.enterText((EditText) solo
					.findViewById("com.tntu.easyenglish.R.id.loginEt"),
					"insearching");
			
			assertTrue(
					"Wait for edit text (id: com.tntu.easyenglish.R.id.passEt) failed.",
					solo.waitForEditTextById(
							"com.tntu.easyenglish.R.id.passEt", 2000));
			solo.enterText((EditText) solo
					.findViewById("com.tntu.easyenglish.R.id.passEt"), "130592");
			assertTrue(
					"Wait for text (id: com.tntu.easyenglish.R.id.submitTv) failed.",
					solo.waitForTextById("com.tntu.easyenglish.R.id.submitTv",
							2000));
			solo.clickOnText((TextView) solo
					.findViewById("com.tntu.easyenglish.R.id.submitTv"));
		} catch (AssertionFailedError e) {
			solo.fail(
					"com.tntu.easyenglish.test.SplashActivityTest.testRecorded_scr_fail",
					e);
			throw e;
		} catch (Exception e) {
			solo.fail(
					"com.tntu.easyenglish.test.SplashActivityTest.testRecorded_scr_fail",
					e);
			throw e;
		}
	}

}

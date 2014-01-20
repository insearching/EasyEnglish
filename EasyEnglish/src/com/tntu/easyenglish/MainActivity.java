package com.tntu.easyenglish;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;

public class MainActivity extends FragmentActivity {

	private TextView nameTv;
	private ProfilePictureView profilePicture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nameTv = (TextView) findViewById(R.id.greetTv);
		profilePicture = (ProfilePictureView) findViewById(R.id.profilePicture);

		String nameKey = "name";
		String idKey = "id";
		Bundle data = getIntent().getExtras();
		if (data != null) {
			if (data.containsKey(nameKey))
				nameTv.setText("Hello " + data.getString(nameKey) + "!");
			if (data.containsKey(idKey)) {
				profilePicture.setProfileId(data.getString(idKey));
			}
		}
	}

	public void onLogout(View v) {
		// find the active session which can only be facebook in my app
		Session session = Session.getActiveSession();
		// run the closeAndClearTokenInformation which does the following
		// DOCS : Closes the local in-memory Session object and clears any
		// persistent
		// cache related to the Session.
		session.closeAndClearTokenInformation();
		// return the user to the login screen
		startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		// make sure the user can not access the page after he/she is logged out
		// clear the activity stack
		finish();
	}
}

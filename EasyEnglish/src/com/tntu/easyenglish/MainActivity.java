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
		Session session = Session.getActiveSession();
		session.closeAndClearTokenInformation();
		startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		finish();
	}
}

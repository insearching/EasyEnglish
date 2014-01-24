package com.tntu.easyenglish.fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tntu.easyenglish.MainActivity;
import com.tntu.easyenglish.R;

public class MainFragment extends Fragment {
	private View convertView;
	private TextView nameTv;
	private ImageView profileIv;
	private Button logintButt;

	public static MainFragment newInstance(Bundle b) {
		MainFragment f = new MainFragment();
		f.setArguments(b);
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState = getArguments();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initView(inflater);

		Bundle args = savedInstanceState;
		if (args == null) {
			args = getArguments();
		}

		if (args != null)
			if (args.containsKey(MainActivity.NAME_KEY)
					&& args.containsKey(MainActivity.ID_KEY)
					&& args.containsKey(MainActivity.AUTH_KEY)) {
				logintButt
						.setText(getString(R.string.com_facebook_loginview_log_out_button));
				nameTv.setText("Hello " + args.getString(MainActivity.NAME_KEY)
						+ "!");
				MainActivity.AuthType type = MainActivity.AuthType.valueOf(args
						.getString((MainActivity.AUTH_KEY)));
				String id = args.getString(MainActivity.ID_KEY);
				String url = null;
				if (type == MainActivity.AuthType.FACEBOOK) {
					url = "http://graph.facebook.com/" + id
							+ "/picture?type=large";
				} else if (type == MainActivity.AuthType.GOOGLE) {
					url = "https://plus.google.com/s2/photos/profile/" + id
							+ "?sz=180";
				}
				if (url != null)
					new GetProfileImageById().execute(url);
			}
		return convertView;
	}

	class GetProfileImageById extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			String url = params[0];
			URL imgValue = null;
			try {
				imgValue = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			if (imgValue == null)
				return null;
			Bitmap icon = null;
			try {
				icon = BitmapFactory.decodeStream(imgValue.openConnection()
						.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return icon;
		}

		@Override
		protected void onPostExecute(Bitmap icon) {
			super.onPostExecute(icon);
			if (icon == null)
				return;
			profileIv.setImageBitmap(icon);
		}
	}

	private void initView(LayoutInflater inflater) {
		convertView = inflater.inflate(R.layout.main_fragment, null);
		nameTv = (TextView) convertView.findViewById(R.id.greetTv);
		profileIv = (ImageView) convertView.findViewById(R.id.profileIv);
		logintButt = (Button) convertView.findViewById(R.id.logintButt);
	}
}

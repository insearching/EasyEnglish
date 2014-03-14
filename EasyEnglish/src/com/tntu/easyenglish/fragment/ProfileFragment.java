package com.tntu.easyenglish.fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.utils.KeyUtils;

public class ProfileFragment extends Fragment {
	private View convertView;
	private TextView nameTv;
	private ImageView profileIv;

	public static ProfileFragment newInstance(Bundle bundle) {
		ProfileFragment fragment = new ProfileFragment();
		fragment.setArguments(bundle);
		return fragment;
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
			if (args.containsKey(KeyUtils.NAME_KEY)
					&& args.containsKey(KeyUtils.ID_KEY)
					&& args.containsKey(KeyUtils.AUTH_KEY)) {
				nameTv.setText("Hello "
						+ args.getString(KeyUtils.NAME_KEY) + "!");
				KeyUtils.AuthType type = KeyUtils.AuthType
						.valueOf(args.getString((KeyUtils.AUTH_KEY)));
				String id = args.getString(KeyUtils.ID_KEY);
				String url = null;
				if (type == KeyUtils.AuthType.FACEBOOK) {
					url = "http://graph.facebook.com/" + id
							+ "/picture?type=large";
				} else if (type == KeyUtils.AuthType.GOOGLE) {
					url = "https://plus.google.com/s2/photos/profile/" + id
							+ "?sz=180";
				}
				if (url != null)
					new GetProfileImageById().execute(url);
			}
		return convertView;
	}

	private void initView(LayoutInflater inflater) {
		convertView = inflater.inflate(R.layout.profile_fragment, null);
		nameTv = (TextView) convertView.findViewById(R.id.greetTv);
		profileIv = (ImageView) convertView.findViewById(R.id.profileIv);
		Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.com_facebook_profile_picture_blank_square);
		profileIv.setImageBitmap(getRoundedShape(bitmap));
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
			profileIv.setImageBitmap(getRoundedShape(icon));
		}
	}

	public Bitmap getRoundedShape(Bitmap bitmap) {
		Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(10);
		Canvas c = new Canvas(circleBitmap);
		// This draw a circle of Gerycolor which will be the border of image.
		c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
				bitmap.getWidth() / 2, paint);
		BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);
		paint.setAntiAlias(true);
		paint.setShader(shader);
		// This will draw the image.
		c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
				bitmap.getWidth() / 2 -5, paint);
		return circleBitmap;
	}
}

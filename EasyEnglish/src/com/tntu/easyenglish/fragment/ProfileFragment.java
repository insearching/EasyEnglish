package com.tntu.easyenglish.fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
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
import android.widget.Toast;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.User;
import com.tntu.easyenglish.utils.ImageLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class ProfileFragment extends Fragment implements JSONCompleteListener {
	private View convertView;
	private TextView nameTv;
	private ImageView profileIv;

	private String url = null;
	private String id = null;
	private String apiKey = null;
	private KeyUtils.AuthType authType = KeyUtils.AuthType.NONE;

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

		if (args != null) {
			// if (args.containsKey(KeyUtils.NAME_KEY)
			// && args.containsKey(KeyUtils.ID_KEY)
			// && args.containsKey(KeyUtils.AUTH_KEY)) {
			// nameTv.setText("Hello "
			// + args.getString(KeyUtils.NAME_KEY) + "!");
			// KeyUtils.AuthType type = KeyUtils.AuthType
			// .valueOf(args.getString((KeyUtils.AUTH_KEY)));
			// String id = args.getString(KeyUtils.ID_KEY);
			// String url = null;
			// if(type == KeyUtils.AuthType.NATIVE){
			//
			// }
			// else if (type == KeyUtils.AuthType.FACEBOOK) {
			// url = "http://graph.facebook.com/" + id
			// + "/picture?type=large";
			// } else if (type == KeyUtils.AuthType.GOOGLE) {
			// url = "https://plus.google.com/s2/photos/profile/" + id
			// + "?sz=180";
			// }
			// if (url != null)
			// new GetProfileImageById().execute(url);
			// }

			if (args.containsKey(KeyUtils.NAME_KEY)) {
				// nameTv.setText("Hello "
				// + args.getString(KeyUtils.NAME_KEY) + "!");
				//name = args.getString(KeyUtils.NAME_KEY);
			}

			if (args.containsKey(KeyUtils.ID_KEY)) {
				id = args.getString(KeyUtils.ID_KEY);
			}

			if (args.containsKey(KeyUtils.API_KEY)) {
				apiKey = args.getString(KeyUtils.API_KEY);
			}

			if (args.containsKey(KeyUtils.AUTH_KEY)) {
				authType = (KeyUtils.AuthType) args
						.getSerializable(KeyUtils.AUTH_KEY);

			}

			if (authType != KeyUtils.AuthType.NONE) {
				if (apiKey != null && authType == KeyUtils.AuthType.NATIVE) {
					String url = "http://easy-english.yzi.me/api/getUserDetails?api_key="
							+ apiKey;
					RESTClient client = new RESTClient(this);
					client.execute(url);

				} else if (id != null && authType == KeyUtils.AuthType.FACEBOOK) {
					url = "http://graph.facebook.com/" + id
							+ "/picture?type=large";
				} else if (id != null && authType == KeyUtils.AuthType.GOOGLE) {
					url = "https://plus.google.com/s2/photos/profile/" + id
							+ "?sz=180";
				}
			}
		}
		return convertView;
	}

	private void initView(LayoutInflater inflater) {
		convertView = inflater.inflate(R.layout.profile_fragment, null);
		nameTv = (TextView) convertView.findViewById(R.id.greetTv);
		profileIv = (ImageView) convertView.findViewById(R.id.profileIv);
		Bitmap bitmap = BitmapFactory.decodeResource(getActivity()
				.getResources(),
				R.drawable.com_facebook_profile_picture_blank_square);
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
		paint.setColor(getResources().getColor(R.color.yellow));
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
				bitmap.getWidth() / 2 - 5, paint);
		return circleBitmap;
	}

	@Override
	public void onRemoteCallComplete(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status != null && status.equals(JSONUtils.SUCCESS_TRUE)) {
			User user = JSONUtils.getUserData(json);
			String avatar = user.getAvatar();
			String login = user.getLogin();
			
			Pattern pattern = Pattern.compile("\\?s=[\\d\\.-]*");
			Matcher matcher = pattern.matcher(avatar);
			String width = null;
			if (matcher.find()){
				width = matcher.group(0);
				avatar = avatar.replace(width, "?s=300");
			}
			
			ImageLoader loader = new ImageLoader(getActivity());
			loader.displayImage(avatar, profileIv, true);
			
			nameTv.setText(getString(R.string.hello) + login + "!");
		}
		else {
			Toast.makeText(getActivity(), getString(R.string.failed_to_retrieve_data), Toast.LENGTH_SHORT).show();
		}
	}
}

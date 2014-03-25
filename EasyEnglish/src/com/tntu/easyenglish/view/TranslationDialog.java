package com.tntu.easyenglish.view;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.entity.Translation;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListener;

public class TranslationDialog implements JSONCompleteListener {

	private Context context;
	private RESTClient client;
	private Dialog dialog;
	private String apiKey;

	private TextView origWordTv;
	private ImageView wordIv;
	private RelativeLayout contentLayout;
	private LinearLayout transLayout;

	public TranslationDialog(Context context, String apiKey) {
		client = new RESTClient(TranslationDialog.this);
		this.context = context;
		this.apiKey = apiKey;
	}

	public void initContentText(TextView contentTv, String text) {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialog.getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.add_word_dialog);

		origWordTv = (TextView) dialog.findViewById(R.id.origWordTv);
		wordIv = (ImageView) dialog.findViewById(R.id.wordIv);
		contentLayout = (RelativeLayout) dialog.findViewById(R.id.contentRl);
		contentLayout.setVisibility(View.INVISIBLE);
		transLayout = (LinearLayout) dialog.findViewById(R.id.translationLl);
		((TextView) dialog.findViewById(R.id.cancelTv)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		contentTv.setMovementMethod(LinkMovementMethod.getInstance());
		contentTv.setText(text, BufferType.SPANNABLE);
		Spannable spans = (Spannable) contentTv.getText();
		BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
		iterator.setText(text);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
				.next()) {
			String possibleWord = text.substring(start, end);
			if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
				ClickableSpan clickSpan = getClickableSpan(possibleWord);
				spans.setSpan(clickSpan, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	private ClickableSpan getClickableSpan(final String word) {
		return new ClickableSpan() {
			final String mWord;
			{
				mWord = word;
			}

			@Override
			public void onClick(View widget) {

				if (dialog.isShowing()) {
					dialog.dismiss();
					return;
				}

				origWordTv.setText(mWord + " — "
						+ context.getString(R.string.select_translation));
				client = new RESTClient(TranslationDialog.this);
				client.execute("http://easy-english.yzi.me/api/translate?api_key="
						+ apiKey + "&text=" + mWord);
				dialog.show();
				contentLayout.setVisibility(View.INVISIBLE);
			}

			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
			}
		};
	}

	@Override
	public void onRemoteCallComplete(String json) {
		ArrayList<Translation> data = JSONUtils.getTranslation(json);

		wordIv.setImageResource(R.drawable.ic_launcher);

		transLayout.removeAllViews();
		for (int i = 0; i < data.size(); i++) {
			Translation tr = data.get(i);
			final String translation = tr.getText();
			final String imageUrl = tr.getImageUrl();

			UrlImageViewHelper.setUrlDrawable(wordIv, imageUrl,
					R.drawable.ic_launcher, new UrlImageViewCallback() {
						@Override
						public void onLoaded(ImageView imageView,
								Bitmap loadedBitmap, String url,
								boolean loadedFromCache) {

							if (!loadedFromCache) {
								ScaleAnimation scale = new ScaleAnimation(0, 1,
										0, 1, ScaleAnimation.RELATIVE_TO_SELF,
										.5f, ScaleAnimation.RELATIVE_TO_SELF,
										.5f);
								scale.setDuration(500);
								scale.setInterpolator(new OvershootInterpolator());
								imageView.startAnimation(scale);
							}
						}
					});

			TextView transTv = new TextView(context);
			transTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			transTv.setText(i + 1 + ". " + translation);
			transTv.setClickable(true);
			transTv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Toast.makeText(
							context,
							translation
									+ " "
									+ context
											.getString(R.string.added_to_dictionary),
							Toast.LENGTH_LONG).show();
				}
			});
			transLayout.addView(transTv);
		}

		contentLayout.setVisibility(View.VISIBLE);
		((ProgressBar) dialog.findViewById(R.id.loadPb))
				.setVisibility(View.INVISIBLE);
	}
}

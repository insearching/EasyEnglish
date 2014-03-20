package com.tntu.easyenglish.view;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.tntu.easyenglish.R;
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
		transLayout = (LinearLayout) dialog.findViewById(R.id.translationLl);

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
				((ProgressBar) dialog.findViewById(R.id.loadPb))
						.setVisibility(View.VISIBLE);
				transLayout.setVisibility(View.GONE);

				origWordTv.setText(mWord + " — "
						+ context.getString(R.string.select_translation));
				client = new RESTClient(TranslationDialog.this);
				client.execute("http://easy-english.yzi.me/api/translate?api_key="
						+ apiKey + "&text=" + mWord);
				dialog.show();
			}

			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
			}
		};
	}

	@Override
	public void onRemoteCallComplete(String json) {
		ArrayList<String> data = JSONUtils.getTranslation(json);

		wordIv.setImageResource(R.drawable.ic_launcher);

		transLayout.removeAllViews();
		for (int i = 0; i < data.size(); i++) {
			final String translation = data.get(i);
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

		transLayout.setVisibility(View.VISIBLE);
		((ProgressBar) dialog.findViewById(R.id.loadPb))
				.setVisibility(View.INVISIBLE);
	}
}

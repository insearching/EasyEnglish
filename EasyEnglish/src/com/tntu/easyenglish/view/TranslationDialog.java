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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.TranslationAdatper;
import com.tntu.easyenglish.entity.Translation;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.RESTClient;
import com.tntu.easyenglish.utils.RESTClient.JSONCompleteListenerMethod;

public class TranslationDialog implements JSONCompleteListenerMethod {

	private Context context;
	private Dialog dialog;
	private String apiKey;
	private String mWord;

	private TextView origWordTv;
	private RelativeLayout contentLayout;
	private ListView transLv;
	private CustomProgressBar loadPb;
	

	private static final String ADD_TO_DICTIONARY = "addWordToDictionary";
	private static final String TRANSLATE = "translate";

	public TranslationDialog(Context context, String apiKey) {
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
		contentLayout = (RelativeLayout) dialog.findViewById(R.id.contentRl);
		contentLayout.setVisibility(View.INVISIBLE);
		transLv = (ListView) dialog.findViewById(R.id.transLv);
		loadPb = (CustomProgressBar) dialog.findViewById(R.id.loadPb);

		((TextView) dialog.findViewById(R.id.cancelTv))
				.setOnClickListener(new OnClickListener() {
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
			final String sWord;
			{
				mWord = sWord = word;
			}

			@Override
			public void onClick(View widget) {
				if (dialog.isShowing()) {
					dialog.dismiss();
					return;
				}

				origWordTv.setText(sWord + " — "
						+ context.getString(R.string.select_translation));
				RESTClient client = new RESTClient(TranslationDialog.this,
						TRANSLATE);
				String url = "http://easy-english.yzi.me/api/translate?api_key="
						+ apiKey + "&text=" + sWord;

				client.execute(url);
				dialog.show();
				contentLayout.setVisibility(View.INVISIBLE);
				loadPb.setVisibility(View.VISIBLE);
			}

			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
			}
		};
	}

	@Override
	public void onRemoteCallComplete(String json, String method) {
		if (JSONUtils.getResponseStatus(json) == JSONUtils.SUCCESS_TRUE) {
			if (method.equals(ADD_TO_DICTIONARY)) {
				Toast.makeText(
						context,
						"Word"
								+ " "
								+ context
										.getString(R.string.added_to_dictionary),
						Toast.LENGTH_LONG).show();
			}

			else if (method.equals(TRANSLATE)) {
				ArrayList<Translation> data = JSONUtils.getTranslation(json);

				final TranslationAdatper adapter = new TranslationAdatper(
						context, data);
				transLv.setAdapter(adapter);
				transLv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						addWordToDictionary(mWord, adapter.getItem(position)
								.getText());
					}
				});

				contentLayout.setVisibility(View.VISIBLE);
				loadPb.setVisibility(View.INVISIBLE);
			}
		} else {
			Toast.makeText(context,
					context.getString(R.string.failed_to_retrieve_data),
					Toast.LENGTH_LONG).show();
			dialog.dismiss();
		}
	}

	private void addWordToDictionary(String word, String translation) {
		RESTClient client = new RESTClient(this, ADD_TO_DICTIONARY);
		String request = "http://easy-english.yzi.me/api/addToDictionary?api_key="
				+ apiKey + "&eng_text=" + word + "&translation=" + translation;
		client.execute(request);
	}
}

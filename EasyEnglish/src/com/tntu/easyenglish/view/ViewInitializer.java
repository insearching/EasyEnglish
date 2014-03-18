package com.tntu.easyenglish.view;

import java.text.BreakIterator;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.tntu.easyenglish.R;

public class ViewInitializer {

	
	public static void initContentText(Context context, TextView contentTv, String text){
		Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.getWindow().addFlags(
//				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.add_word_dialog);
		
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
				ClickableSpan clickSpan = getClickableSpan(dialog, possibleWord);
				spans.setSpan(clickSpan, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
	
	private static ClickableSpan getClickableSpan(final Dialog dialog, final String word) {
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
				// set the custom dialog components - text, image and button
				TextView text = (TextView) dialog.findViewById(R.id.origWordTv);
				text.setText(mWord);
				ImageView image = (ImageView) dialog.findViewById(R.id.wordIv);
				image.setImageResource(R.drawable.ic_launcher);

				dialog.show();
			}

			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
			}
		};
	}
}
